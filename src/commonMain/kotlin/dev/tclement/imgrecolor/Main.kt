/*
 * Copyright (C) 2023  T. Clément (@tclement0922) <dev.tclement0922@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.tclement.imgrecolor

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import dev.kdrag0n.colorkt.conversion.ConversionGraph
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.ucs.lch.Oklch
import dev.tclement.imgrecolor.Rgba.Companion.toRgba
import dev.tclement.imgrecolor.algorithm.ColorComparisonAlgorithm
import dev.tclement.imgrecolor.algorithm.ColorComparisonAlgorithm.Companion.getColorDistance
import dev.tclement.imgrecolor.algorithm.cie76
import dev.tclement.imgrecolor.algorithm.cie94
import dev.tclement.imgrecolor.algorithm.ciede2000
import dev.tclement.imgrecolor.provider.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.set

object Main: CliktCommand(), CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Default) {
    private val palette by option("-p", "--palette")
        .help("""The color palette, depends on the provider :
            |for "gogh" : The file name without the extensions, file names are listed here : https://github.com/Gogh-Co/Gogh/tree/master/themes ;
            |for "url" : The url of a file containing hex colors (like "#FFFFFF");
            |for "local" : The local path of a file containing hex colors (like "#FFFFFF");
            |for "text" : A comma separated list of hex colors (like "#FFFFF,#FF00FF,#FFFF00").""".trimMargin())
        .required()
    private val paletteProviderStr by option("-P", "--palette-provider")
        .help("""The palette source name.""")
        .choice("gogh", "local", "url", "text", ignoreCase = true)
        .default(Constants.OPTION_SCHEME_PROVIDER_DEFAULT)
    private val chroma by option("-c", "--chroma")
        .help("""Keep/do not keep the original color chroma (colorfulness)""")
        .flag("-C", "--no-chroma", default = Constants.OPTION_CHROMA_DEFAULT)
    private val lightness by option("-l", "--lightness")
        .help("""Keep/do not keep the original color lightness""")
        .flag("-L", "--no-lightness", default = Constants.OPTION_LIGHTNESS_DEFAULT)
    private val showProgress by option("--hide-progress")
        .help("""Hide the progress""")
        .flag(default = Constants.OPTION_PROGRESS_DEFAULT)
    private val colorComparisonAlgorithmStr by option("--color-comp-alg")
        .choice("CIE76", "CIE94", "CIEDE2000", ignoreCase = true)
        .help("""The color comparison algorithm used to compute new colors. CIEDE2000 is the more efficient and CIE76 the faster. Default value : ${Constants.OPTION_COLOR_COMP_ALG_DEFAULT}""")
        .default(Constants.OPTION_COLOR_COMP_ALG_DEFAULT)
    private val excludedColors by option("-e", "--excluded-colors")
        .help("""Colors that will be excluded from the new colors. Must be a comma separated list of hex colors (like "#FFFFF,#FF00FF,#FFFF00")""")
        .multiple()
    private val inputFile by argument(name = "INPUT_FILE")
    private val outputFile by argument(name = "OUTPUT_FILE")

    private val colorSet = mutableSetOf<Srgb>()
    private val computed = mutableMapOf<Srgb, Srgb>() // Save already computed correspondences to greatly improve speed

    override fun run() = runBlocking { runSuspend() }

    private val copyFromOptions: Oklch.(Oklch) -> Oklch by lazy { when {
        lightness && chroma -> { old ->
            old.copy(hue = hue)
        }
        lightness -> { old ->
            copy(lightness = old.lightness)
        }
        chroma -> { old ->
            copy(chroma = old.chroma)
        }
        else -> { _ -> this }
    } }

    private suspend fun runSuspend() {
        log("Reading palette from provider \"$paletteProviderStr\"")
        val scheme = try {
            when (paletteProviderStr) {
                "gogh" -> readGoghScheme(palette)
                "url" -> readColorsFromUrl(palette)
                "local" -> readColorsFromFile(palette)
                "text" -> readRgb8Colors(palette)
                else -> logError("Scheme provider $paletteProviderStr unknown")
            }
        } catch (e: Exception) {
            logError("Failed to read scheme : ${e.message}")
        }
        colorSet.addAll(scheme.filter { color ->
            color.toHex() !in excludedColors.map { excluded -> excluded.lowercase() }
        })
        if (colorSet.isEmpty()) {
            logError("Unable to find any color in the provided color scheme")
        }
        log("Decoding image")
        val image = readImageFromFile(inputFile)
        log("Computing new colors...")
        val progress = MutableStateFlow(0)
        @Suppress("SuspendFunctionOnCoroutineScope") val progressJob = if (showProgress) launch {
            val imageSize = image.width * image.height
            val steps = imageSize / 100
            progress.collect {
                log("${it / steps}%", overridePrevious = true, newLine = false)
            }
        } else null
        if (colorSet.size == 1) {
            image.forEachIndexed { index: Int, pixel: Rgba ->
                progress.tryEmit(index)
                image[index] = computed[pixel.toSrgb()]?.toRgba(a = pixel.a) ?: colorSet
                    .single()
                    .convert<Oklch>()
                    .copyFromOptions(pixel.convert())
                    .convert<Rgba>().also { computed[pixel.toSrgb()] = it.toSrgb() }
            }
        } else {
            image.forEachIndexed { index: Int, pixel: Rgba ->
                progress.tryEmit(index)
                image[index] = getNearestColor(pixel)
            }
        }
        progressJob?.cancelAndJoin()
        log("Writing image to file $outputFile", overridePrevious = showProgress)
        writeImageToFile(image, outputFile)
        log("Done !")
    }

    private val colorComparisonAlgorithm: ColorComparisonAlgorithm by lazy {
        when (colorComparisonAlgorithmStr) {
            "CIE76" -> cie76()
            "CIE94" -> cie94()
            "CIEDE2000" -> ciede2000()
            else -> logError("Unknown color comparison algorithm : $colorComparisonAlgorithm")
        }
    }

    private fun getNearestColor(color: Rgba): Rgba {
        computed[color.toSrgb()]?.let {
            return it.toRgba(color.a)
        }
        val colorLch = color.convert<Oklch>()
        val nearest = (colorSet.map { it.convert<Oklch>() } + colorSet.map { it.convert<Oklch>().copyFromOptions(colorLch) })
            .minBy {
                colorComparisonAlgorithm.getColorDistance(it, colorLch)
            }
            .copyFromOptions(colorLch).convert<Srgb>()
        computed[color.toSrgb()] = nearest
        return nearest.toRgba(a = color.a)
    }

    init {
        ConversionGraph.add<Rgba, Srgb> { it.toSrgb() }
        ConversionGraph.add<Srgb, Rgba> { it.toRgba() }
    }
}

fun main(vararg args: String) = Main.main(args)

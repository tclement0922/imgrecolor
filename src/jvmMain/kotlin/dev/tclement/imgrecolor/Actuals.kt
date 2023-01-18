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

@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.tclement.imgrecolor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import javax.imageio.ImageIO

actual suspend fun readTextFromUrl(url: String): String {
    try {
        return URL(url).readText()
    } catch (e: MalformedURLException) {
        logError("The provided url ($url) is invalid : ${e.message}")
    }
}

actual suspend fun readTextFromFile(path: String): String {
    val file = File(path)
    if (file.exists() && file.isFile) {
        return file.readText()
    } else {
        logError("File $path does not exists or is a directory")
    }
}

actual fun exitProcess(status: Int): Nothing {
    kotlin.system.exitProcess(status)
}

actual suspend fun readImageFromFile(path: String): DecodedImage {
    val image = withContext(Dispatchers.IO) {
        try {
            ImageIO.read(File(path))
        } catch (e: IOException) {
            logError("Unable to read the image : ${e.message}")
        }
    } ?: logError("Unable to read the image (ImageIO.read returned null)")
    return DecodedImage(image)
}

actual suspend fun writeImageToFile(image: DecodedImage, path: String) = withContext(Dispatchers.IO) {
    if (!ImageIO.write(image.bufferedImage, "png", File(path))) {
        logError("Unable to write image to file (ImageIO.write returned null)")
    }
}
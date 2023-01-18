/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2023 T. Clément (@tclement0922) <dev.tclement0922@gmail.com>
 */



package dev.tclement.imgrecolor.algorithm

import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.colorkt.ucs.lab.Lab
import dev.kdrag0n.colorkt.ucs.lab.Oklab
import dev.kdrag0n.colorkt.ucs.lch.CieLch
import dev.kdrag0n.colorkt.ucs.lch.CieLch.Companion.toCieLch
import dev.kdrag0n.colorkt.ucs.lch.Lch
import dev.kdrag0n.colorkt.ucs.lch.Oklch
import dev.kdrag0n.colorkt.ucs.lch.Oklch.Companion.toOklch

fun interface ColorComparisonAlgorithm {
    fun getColorDistance(lab1: Lab, lab2: Lab, lch1: Lch, lch2: Lch): Double

    companion object {
        fun ColorComparisonAlgorithm.getColorDistance(lab1: Oklab, lab2: Oklab) = getColorDistance(lab1, lab2, lab1.toOklch(), lab2.toOklch())

        fun ColorComparisonAlgorithm.getColorDistance(lch1: Oklch, lch2: Oklch) = getColorDistance(lch1.toOklab(), lch2.toOklab(), lch1, lch2)

        fun ColorComparisonAlgorithm.getColorDistance(lab1: CieLab, lab2: CieLab) = getColorDistance(lab1, lab2, lab1.toCieLch(), lab2.toCieLch())

        fun ColorComparisonAlgorithm.getColorDistance(lch1: CieLch, lch2: CieLch) = getColorDistance(lch1.toCieLab(), lch2.toCieLab(), lch1, lch2)
    }
}
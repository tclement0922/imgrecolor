/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2023 T. Clément (@tclement0922) <dev.tclement0922@gmail.com>
 */

package dev.tclement.imgrecolor.algorithm

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Implementation of the CIE94 Algorithm
 *
 * Based on this website :
 *  - https://en.wikipedia.org/wiki/Color_difference#CIE94
 */
@Suppress("LocalVariableName", "DuplicatedCode")
fun cie94() = ColorComparisonAlgorithm { lab1, lab2, lch1, lch2 ->
    val L1 = lab1.L
    val L2 = lab2.L
    val a1 = lab1.a
    val a2 = lab2.a
    val b1 = lab1.b
    val b2 = lab2.b
    val C1 = lch1.chroma
    val C2 = lch2.chroma

    val kC = 1.0
    val kH = 1.0
    val kL = 1.0
    val K1 = 0.045
    val K2 = 0.015

    val deltaL = L1 - L2

    val deltaC = C1 - C2
    val deltaa = a1 - a2
    val deltab = b1 - b2

    val deltaH = sqrt(deltaa.pow(2) + deltab.pow(2) - deltaC.pow(2))

    val SL = 1.0
    val SC = 1 + K1 * C1
    val SH = 1 + K2 * C1

    sqrt((deltaL / (kL * SL)).pow(2) + (deltaC / (kC * SC)).pow(2) + (deltaH / (kH * SH)).pow(2))
}
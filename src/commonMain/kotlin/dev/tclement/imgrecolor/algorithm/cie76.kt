/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2023 T. Clément (@tclement0922) <dev.tclement0922@gmail.com>
 */

package dev.tclement.imgrecolor.algorithm

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Implementation of the CIE76 Algorithm
 *
 * Based on this website :
 *  - https://en.wikipedia.org/wiki/Color_difference#CIE76
 */
@Suppress("LocalVariableName", "DuplicatedCode")
fun cie76() = ColorComparisonAlgorithm { lab1, lab2, _, _ ->
    val L1 = lab1.L
    val L2 = lab2.L
    val a1 = lab1.a
    val a2 = lab2.a
    val b1 = lab1.b
    val b2 = lab2.b

    sqrt((L2 - L1).pow(2) + (a2 - a1).pow(2) + (b2 - b1).pow(2))
}
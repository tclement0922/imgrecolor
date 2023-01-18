/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2023 T. Clément (@tclement0922) <dev.tclement0922@gmail.com>
 */

package dev.tclement.imgrecolor.algorithm

import kotlin.math.*

/**
 * Implementation of the CIEDE2000 Algorithm
 *
 * Based on this document :
 *  - https://hajim.rochester.edu/ece/sites/gsharma/ciede2000/ciede2000noteCRNA.pdf
 *
 * Even though it is the most accurate, the performance cost of this algorithm could be quite high for some
 * devices/targets
 */
@Suppress("LocalVariableName", "DuplicatedCode")
fun ciede2000() = ColorComparisonAlgorithm { lab1, lab2, lch1, lch2 ->
    val L1 = lab1.L
    val L2 = lab2.L
    val a1 = lab1.a
    val a2 = lab2.a
    val b1 = lab1.b
    val b2 = lab2.b
    val C1 = lch1.chroma
    val C2 = lch2.chroma

    val kL = 1.0
    val kC = 1.0
    val kH = 1.0

    val pow25to7 = 25.0.pow(7)

    val deltaLPrime = L2 - L1

    val barL = (L1 + L2) / 2
    val barC = (C1 + C2) / 2

    val G = 0.5 * (1 - sqrt(barC.pow(7) / (barC.pow(7) + pow25to7)))
    val a1Prime = a1 * (1 + G)
    val a2Prime = a2 * (1 + G)

    val C1Prime = sqrt(a1Prime.pow(2) + b1.pow(2))
    val C2Prime = sqrt(a2Prime.pow(2) + b2.pow(2))

    val deltaCPrime = C2Prime - C1Prime

    val barCPrime = (C1Prime + C2Prime) / 2

    val h1Prime = if (b1 == 0.0 && a1Prime == 0.0) {
        0.0
    } else if (b1 > 0.0) {
        atan2(b1, a1Prime).radToDeg()
    } else {
        atan2(b1, a1Prime).radToDeg() + 360
    }
    val h2Prime = if (b2 == 0.0 && a2Prime == 0.0) {
        0.0
    } else if (b2 > 0.0) {
        atan2(b2, a2Prime).radToDeg()
    } else {
        atan2(b2, a2Prime).radToDeg() + 360
    }

    val deltahPrime = if (C1Prime * C2Prime == 0.0) {
        0.0
    } else if (abs(h1Prime - h2Prime) <= 180) {
        h2Prime - h1Prime
    } else if (h2Prime <= h1Prime) {
        h2Prime - h1Prime + 360
    } else {
        h2Prime - h1Prime - 360
    }

    val deltaHPrime = 2 * sqrt(C1Prime * C2Prime) * sin((deltahPrime / 2).degToRad())

    val barHPrime = if (C1Prime * C2Prime == 0.0) {
        h1Prime + h2Prime
    } else if (abs(h1Prime - h2Prime) <= 180) {
        (h1Prime + h2Prime) / 2
    } else if (h1Prime + h2Prime < 360) {
        (h1Prime + h2Prime + 360) / 2
    } else {
        (h1Prime + h2Prime - 360) / 2
    }

    val T = 1 - 0.17 * cos((barHPrime - 30).degToRad()) + 0.24 * cos((2 * barHPrime).degToRad()) + 0.32 * cos((3 * barHPrime + 6).degToRad()) - 0.20 * cos((4 * barHPrime - 63).degToRad())

    val deltaTheta = 30 * exp(-((barHPrime - 275) / 25).pow(2))

    val SL = 1 + (0.015 * (barL - 50).pow(2)) / sqrt(20 + (barL - 50).pow(2))
    val SC = 1 + 0.045 * barCPrime
    val SH = 1 + 0.015 * barCPrime * T

    val RC = 2 * sqrt(barCPrime.pow(7) / (barCPrime.pow(7) + pow25to7))
    val RT = -RC * sin((2 * deltaTheta).degToRad())

    sqrt((deltaLPrime / (kL * SL)).pow(2) + (deltaCPrime / (kC * SC)).pow(2) + (deltaHPrime / (kH * SH)).pow(2) + RT * (deltaCPrime / (kC * SC)) * (deltaHPrime / (kH * SH)))
}

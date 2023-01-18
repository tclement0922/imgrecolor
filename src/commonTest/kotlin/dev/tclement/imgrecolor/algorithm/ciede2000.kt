/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2023 T. Clément (@tclement0922) <dev.tclement0922@gmail.com>
 */

package dev.tclement.imgrecolor.algorithm

import dev.kdrag0n.colorkt.ucs.lab.Oklab
import dev.tclement.imgrecolor.algorithm.ColorComparisonAlgorithm.Companion.getColorDistance
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for the CIEDE2000 algorithm. Test data from `TABLE I. CIEDE2000 total color difference test data` from
 * the document https://hajim.rochester.edu/ece/sites/gsharma/ciede2000/ciede2000noteCRNA.pdf
 *
 * Those tests have been written to make sure this algorithm works in any possible cases
 */
class CIEDE2000Tests {
    private val algorithm = ciede2000()

    @Suppress("CovariantEquals")
    private infix fun Pair<Oklab, Oklab>.equals(expectedResult: Double) {
        assertEquals(expectedResult, algorithm.getColorDistance(first, second).round(4))
    }

    @Suppress("LocalVariableName")
    private fun lab(L: Double, a: Double, b: Double) = Oklab(L, a, b)

    @Test
    fun pair01() = lab(L = 50.0, a = 2.6772, b = -79.7751) to lab(L = 50.0, a = 0.0, b = -82.7485) equals 2.0425

    @Test
    fun pair02() = lab(L = 50.0, a = 3.1571, b = -77.2803) to lab(L = 50.0, a = 0.0, b = -82.7485) equals 2.8615

    @Test
    fun pair03() = lab(L = 50.0, a = 2.8361, b = -74.02) to lab(L = 50.0, a = 0.0, b = -82.7485) equals 3.4412

    @Test
    fun pair04() = lab(L = 50.0, a = -1.3802, b = -84.2814) to lab(L = 50.0, a = 0.0, b = -82.7485) equals 1.0

    @Test
    fun pair05() = lab(L = 50.0, a = -1.1848, b = -84.8006) to lab(L = 50.0, a = 0.0, b = -82.7485) equals 1.0

    @Test
    fun pair06() = lab(L = 50.0, a = -0.9009, b = -85.5211) to lab(L = 50.0, a = 0.0, b = -82.7485) equals 1.0

    @Test
    fun pair07() = lab(L = 50.0, a = 0.0, b = 0.0) to lab(L = 50.0, a = -1.0, b = 2.0) equals 2.3669

    @Test
    fun pair08() = lab(L = 50.0, a = -1.0, b = 2.0) to lab(L = 50.0, a = 0.0, b = 0.0) equals 2.3669

    @Suppress("CovariantEquals")
    @Test
    fun pair09() = lab(L = 50.0, a = 2.49, b = -0.001) to lab(L = 50.0, a = -2.49, b = 0.0001) equals 7.1792

    @Test
    fun pair10() = lab(L = 50.0, a = 2.49, b = -0.001) to lab(L = 50.0, a = -2.49, b = 0.001) equals 7.1792

    @Test
    fun pair11() = lab(L = 50.0, a = 2.49, b = -0.001) to lab(L = 50.0, a = -2.49, b = 0.0011) equals 7.2195

    @Test
    fun pair12() = lab(L = 50.0, a = 2.49, b = -0.001) to lab(L = 50.0, a = -2.49, b = 0.0012) equals 7.2195

    @Test
    fun pair13() = lab(L = 50.0, a = -0.001, b = 2.49) to lab(L = 50.0, a = 0.0009, b = -2.49) equals 4.8045

    @Test
    fun pair14() = lab(L = 50.0, a = -0.001, b = 2.49) to lab(L = 50.0, a = 0.001, b = -2.49) equals 4.8045

    @Test
    fun pair15() = lab(L = 50.0, a = -0.001, b = 2.49) to lab(L = 50.0, a = 0.0011, b = -2.49) equals 4.7461

    @Test
    fun pair16() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 50.0, a = 0.0, b = -2.5) equals 4.3065

    @Test
    fun pair17() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 73.0, a = 25.0, b = -18.0) equals 27.1492

    @Test
    fun pair18() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 61.0, a = -5.0, b = 29.0) equals 22.8977

    @Test
    fun pair19() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 56.0, a = -27.0, b = -3.0) equals 31.9030

    @Test
    fun pair20() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 58.0, a = 24.0, b = 15.0) equals 19.4535

    @Test
    fun pair21() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 50.0, a = 3.1736, b = 0.5854) equals 1.0

    @Test
    fun pair22() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 50.0, a = 3.2972, b = 0.0) equals 1.0

    @Test
    fun pair23() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 50.0, a = 1.8634, b = 0.5757) equals 1.0

    @Test
    fun pair24() = lab(L = 50.0, a = 2.5, b = 0.0) to lab(L = 50.0, a = 3.2592, b = 0.335) equals 1.0

    @Test
    fun pair25() = lab(L = 60.2574, a = -34.0099, b = 36.2677) to lab(L = 60.4626, a = -34.1751, b = 39.4387) equals 1.2644

    @Test
    fun pair26() = lab(L = 63.0109, a = -31.0961, b = -5.8663) to lab(L = 62.8187, a = -29.7946, b = -4.0864) equals 1.263

    @Test
    fun pair27() = lab(L = 61.2901, a = 3.7196, b = -5.3901) to lab(L = 61.4292, a = 2.248, b = -4.962) equals 1.8731

    @Test
    fun pair28() = lab(L = 35.0831, a = -44.1164, b = 3.7933) to lab(L = 35.0232, a = -40.0716, b = 1.5901) equals 1.8645

    @Test
    fun pair29() = lab(L = 22.7233, a = 20.0904, b = -46.694) to lab(L = 23.0331, a = 14.973, b = -42.5619) equals 2.0373

    @Test
    fun pair30() = lab(L = 36.4612, a = 47.858, b = 18.3852) to lab(L = 36.2715, a = 50.5065, b = 21.2231) equals 1.4146

    @Test
    fun pair31() = lab(L = 90.8027, a = -2.0831, b = 1.441) to lab(L = 91.1528, a = -1.6435, b = 0.0447) equals 1.4441

    @Test
    fun pair32() = lab(L = 90.9257, a = -0.5406, b = -0.9208) to lab(L = 88.6381, a = -0.8985, b = -0.7239) equals 1.5381

    @Test
    fun pair33() = lab(L = 6.7747, a = -0.2908, b = -2.4247) to lab(L = 5.8714, a = -0.0985, b = -2.2286) equals 0.6377

    @Test
    fun pair34() = lab(L = 2.0776, a = 0.0795, b = -1.1350) to lab(L = 0.9033, a = -0.0636, b = -0.5514) equals 0.9082
}
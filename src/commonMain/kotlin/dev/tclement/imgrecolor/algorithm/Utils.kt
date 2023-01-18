/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2023 T. Clément (@tclement0922) <dev.tclement0922@gmail.com>
 */

package dev.tclement.imgrecolor.algorithm

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToLong

fun Double.round(digits: Int) = (this * 10.0.pow(digits)).roundToLong() / 10.0.pow(digits)

fun Double.radToDeg() = this / PI * 180

fun Double.degToRad() = this / 180 * PI

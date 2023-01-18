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

import dev.kdrag0n.colorkt.rgb.Rgb
import dev.kdrag0n.colorkt.rgb.Srgb
import kotlin.math.roundToInt

actual data class Rgba(private val srgb: Srgb, actual val a: Double) : Rgb by srgb {
    constructor(argb8: Int): this(
        srgb = Srgb(argb8),
        a = ((argb8 shr 24) and 0xff).toDouble() / 255.0
    )

    actual fun toSrgb() = srgb

    fun toArgb8() = (((a * 255.0).roundToInt().coerceIn(0..255)) shl 24) or srgb.toRgb8()

    actual companion object {
        actual fun Srgb.toRgba(a: Double) = Rgba(this, a)
    }
}
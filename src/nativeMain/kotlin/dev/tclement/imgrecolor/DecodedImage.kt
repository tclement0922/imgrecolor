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

import kotlinx.cinterop.*
import kotlin.math.roundToInt

actual data class DecodedImage(
    val pixelsPtr: CArrayPointer<UByteVar>,
    val widthPtr: CPointer<IntVar>,
    val heightPtr: CPointer<IntVar>,
): Iterable<Rgba> {

    actual val width: Int get() = widthPtr.pointed.value
    actual val height: Int get() = heightPtr.pointed.value

    override fun iterator(): Iterator<Rgba> = iterator {
        for (i in 0 until width * height * 4 step 4) {
            yield(Rgba(
                r = pixelsPtr[i],
                g = pixelsPtr[i + 1],
                b = pixelsPtr[i + 2],
                a = pixelsPtr[i + 3]
            ))
        }
    }

    actual operator fun set(index: Int, color: Rgba) {
        pixelsPtr[index * 4] = (color.clamp { r } * 255).roundToInt().toUByte()
        pixelsPtr[index * 4 + 1] = (color.clamp { g } * 255).roundToInt().toUByte()
        pixelsPtr[index * 4 + 2] = (color.clamp { b } * 255).roundToInt().toUByte()
        pixelsPtr[index * 4 + 3] = (color.clamp { a } * 255).roundToInt().toUByte()
    }
}
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

import java.awt.image.BufferedImage

actual data class DecodedImage(
    val bufferedImage: BufferedImage
): Iterable<Rgba> {
    actual val width: Int get() = bufferedImage.width
    actual val height: Int get() = bufferedImage.height

    override fun iterator(): Iterator<Rgba> = iterator {
        for (pixel in bufferedImage.getRGB(0, 0, width, height, null, 0, width)) {
            yield(Rgba(pixel))
        }
    }

    actual operator fun set(index: Int, color: Rgba) {
        bufferedImage.setRGB(index % width, index / width, color.toArgb8())
    }
}
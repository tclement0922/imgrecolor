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

package dev.tclement.imgrecolor.provider

import dev.kdrag0n.colorkt.rgb.Srgb

fun readRgb8Colors(raw: String): Set<Srgb> =
    "#[a-fA-F0-9]{6}".toRegex().findAll(raw).map { result ->
        val r = result.value.substring(1 until 3).toInt(16)
        val g = result.value.substring(3 until 5).toInt(16)
        val b = result.value.substring(5 until 7).toInt(16)
        Srgb(r, g, b)
    }.toSet()

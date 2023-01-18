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

expect object Constants {
    val OPTION_SCHEME_PROVIDER_DEFAULT: String
    val OPTION_CHROMA_DEFAULT: Boolean
    val OPTION_LIGHTNESS_DEFAULT: Boolean
    val OPTION_PROGRESS_DEFAULT: Boolean
    val OPTION_COLOR_COMP_ALG_DEFAULT: String
}
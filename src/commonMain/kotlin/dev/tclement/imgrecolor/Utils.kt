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

import com.soywiz.klock.DateTime
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToLong

fun log(arg: Any, overridePrevious: Boolean = false, newLine: Boolean = true) {
    fun Int.fixLength(length: Int) = toString().padStart(length, '0')
    val text = "${if (overridePrevious) "\r" else ""}[${
        DateTime.nowLocal().let {
        "${it.hours.fixLength(2)}:${it.minutes.fixLength(2)}:${it.seconds.fixLength(2)}.${it.milliseconds.fixLength(3)}"
    }}] $arg"
    if (newLine) println(text) else print(text)
}

inline fun logError(arg: Any, overridePrevious: Boolean = false, newLine: Boolean = true, errorCode: Int = 1): Nothing {
    log("\u001b[31m$arg\u001b[0m", overridePrevious, newLine)
    exitProcess(errorCode)
}
inline fun Rgba.clamp(component: Rgba.() -> Double) = component().coerceIn(0.0..1.0)

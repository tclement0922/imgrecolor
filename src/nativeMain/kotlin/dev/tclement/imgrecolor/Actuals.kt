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

import curl.*
import kotlinx.cinterop.*
import lvandeve.lodepng.*
import nothings.stbi.stbi_image_free
import nothings.stbi.stbi_load
import platform.posix.realpath
import platform.posix.strcat

typealias CStringPtr = CArrayPointer<ByteVar>

actual suspend fun readTextFromUrl(url: String): String = memScoped {
    val curl = curl_easy_init()
    if (curl != null) {
        curl_easy_setopt(curl, CURLOPT_URL, url)
        curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L)
        val string: CStringPtr = allocArray(1_048_576) // Alloc 1 MiB for the string
        val writeFun =
            staticCFunction<CStringPtr, ULong, ULong, CStringPtr, ULong> { inputPtr, size, nmemb, stringRef ->
                strcat(stringRef, inputPtr.toKString())
                size * nmemb
            }
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeFun)
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, string)
        val res = curl_easy_perform(curl)

        if (res != CURLE_OK) {
            logError("Error loading file from url ($url). Error code = $res", errorCode = res.toInt())
        }
        curl_easy_cleanup(curl)
        string.toKString()
    } else {
        logError("Unable to init curl (curl_easy_init returned null)")
    }
}

actual suspend fun readTextFromFile(path: String): String = memScoped {
    val resolvedPath: CStringPtr = allocArray(1_048_576)
    val realPath = realpath(path, resolvedPath)
    if (realPath != null) {
        val returnBuffer = StringBuilder()
        val file = fopen(realPath.toKString(), "r") ?: logError("Cannot open input file $realPath")

        try {
            val readBufferLength = 1_048_576
            val buffer = allocArray<ByteVar>(readBufferLength)
            var line = fgets(buffer, readBufferLength, file)?.toKString()
            while (line != null) {
                returnBuffer.append(line)
                line = fgets(buffer, readBufferLength, file)?.toKString()
            }
        } finally {
            fclose(file)
        }

        return returnBuffer.toString()
    } else {
        logError("Cannot open input file $realPath")
    }
}

actual fun exitProcess(status: Int): Nothing {
    kotlin.system.exitProcess(status)
}

actual suspend fun readImageFromFile(path: String): DecodedImage {
    val width = nativeHeap.alloc<IntVar>()
    val height = nativeHeap.alloc<IntVar>()
    val pixelsPtr = memScoped {
        val resolvedPath: CStringPtr = allocArray(1_048_576)
        val realPath = realpath(path, resolvedPath)
        if (realPath != null) {
            stbi_load(realPath.toKString(), width.ptr, height.ptr, alloc<IntVar>().ptr, 4)
                ?: logError("stb_image was unable to load the image")
        } else {
            logError(readErrno())
        }
    }
    return DecodedImage(
        pixelsPtr = pixelsPtr,
        widthPtr = width.ptr,
        heightPtr = height.ptr
    )
}

actual suspend fun writeImageToFile(image: DecodedImage, path: String) {
    val dir = path.substringBeforeLast('/')
    val file = path.substringAfterLast('/')
    val res = memScoped {
        val resolvedPath: CStringPtr = allocArray(1_048_576)
        val realPath = realpath(dir, resolvedPath)
        if (realPath != null) {
            lodepng_encode32_file("${realPath.toKString()}/$file", image.pixelsPtr, image.widthPtr.pointed.value.toUInt(), image.heightPtr.pointed.value.toUInt())
        } else {
            logError(readErrno())
        }
    }
    if (res != 0u) {
        logError("lodepng was unable to encode the file (error code $res : ${lodepng_error_text(res)?.toKString()})", errorCode = res.toInt())
    }
    stbi_image_free(image.pixelsPtr)
    with(nativeHeap) {
        free(image.widthPtr)
        free(image.heightPtr)
    }
}
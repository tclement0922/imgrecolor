# ImgRecolor

A cli utility that can change the color palette of an image to whatever you want. 

This project was meant to be just an experiment on building a Kotlin Multiplatform CLI application, so there might be
similar projects with better performances and/or results. Also, given the current state of Kotlin Native, the native
builds are much slower (around 4-5x slower) than the JVM build.

## Usage
```
Usage: main [OPTIONS] INPUT_FILE OUTPUT_FILE

Options:
  -p, --palette TEXT               The color palette, depends on the provider
                                   : for "gogh" : The file name without the
                                   extensions, file names are listed here :
                                   https://github.com/Gogh-Co/Gogh/tree/master/themes
                                   ; for "url" : The url of a file containing
                                   hex colors (like "#FFFFFF"); for "local" :
                                   The local path of a file containing hex
                                   colors (like "#FFFFFF"); for "text" : A
                                   comma separated list of hex colors (like
                                   "#FFFFF,#FF00FF,#FFFF00").
  -P, --palette-provider [gogh|local|url|text]
                                   The palette source name.
  -c, --chroma / -C, --no-chroma   Keep/do not keep the original color chroma
                                   (colorfulness)
  -l, --lightness / -L, --no-lightness
                                   Keep/do not keep the original color
                                   lightness
  --hide-progress                  Hide the progress
  --color-comp-alg [CIE76|CIE94|CIEDE2000]
                                   The color comparison algorithm used to
                                   compute new colors. CIEDE2000 is the more
                                   efficient and CIE76 the faster. Default
                                   value : CIEDE2000
  -e, --excluded-colors TEXT       Colors that will be excluded from the new
                                   colors. Must be a comma separated list of
                                   hex colors (like "#FFFFF,#FF00FF,#FFFF00")
  -h, --help                       Show this message and exit
```

## Open-source libraries
### [Color.kt](https://github.com/kdrag0n/colorkt)
> Color.kt is a modern color science library for Kotlin Multiplatform and Java.

Licensed under the MIT License,
`Copyright (c) 2021 Danny Lin <danny@kdrag0n.dev>`

### [Clikt](https://github.com/ajalt/clikt)
> Clikt (pronounced "clicked") is a multiplatform Kotlin library that makes writing command line interfaces simple and intuitive.

Licensed under the Apache License, Version 2.0,
`Copyright 2018-2022 AJ Alt`

### [Klock](https://github.com/korlibs/korge/tree/main/klock)
> Klock is a Date & Time library for Multiplatform Kotlin.

Dual licensed under the Creative Commons Zero 1.0 Universal and the Apache License, Version 2.0

### [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)
> Library support for Kotlin coroutines with multiplatform support.

Licensed under the Apache License, Version 2.0,
`Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.`

### [stb](https://github.com/nothings/stb)
> single-file public domain (or MIT licensed) libraries for C/C++

Licensed under the MIT License,
`Copyright (c) 2017 Sean Barrett`

### [lodepng](https://github.com/lvandeve/lodepng/)
> PNG encoder and decoder in C and C++, without dependencies

Licensed under the Zlib License,
`Copyright (c) 2005-2018 Lode Vandevenne`

### [curl](https://github.com/curl/curl)
> A command line tool and library for transferring data with URL syntax

Licensed under the curl license,
`Copyright (c) 1996 - 2023, Daniel Stenberg, daniel@haxx.se, and many contributors`

## License
### Special cases
Since I am not the author of the color comparison algorithms, just of their Kotlin implementations, I license those 
files under the MIT License (more permissive than the GPLv3) :
- [cie76.kt](src/commonMain/kotlin/dev/tclement/imgrecolor/algorithm/cie76.kt)
- [cie94.kt](src/commonMain/kotlin/dev/tclement/imgrecolor/algorithm/cie94.kt)
- [ciede2000.kt](src/commonMain/kotlin/dev/tclement/imgrecolor/algorithm/ciede2000.kt)
- [ColorComparisonAlgorithm.kt](src/commonMain/kotlin/dev/tclement/imgrecolor/algorithm/ColorComparisonAlgorithm.kt)
- [ciede2000.kt (tests)](src/commonTest/kotlin/dev/tclement/imgrecolor/algorithm/ciede2000.kt)

```
MIT License

Copyright (c) 2023 T. Clément (@tclement0922) <dev.tclement0922@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
[Read license file](LICENSE-ALGORITHMS)

### Rest of the project
The rest of the project is licensed under the GNU General Public License v3.0 :
```
    Copyright (C) 2023  T. Clément (@tclement0922) <dev.tclement0922@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
[Read license file](LICENSE)

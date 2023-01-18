import org.jetbrains.kotlin.gradle.tasks.CInteropProcess

plugins {
    kotlin("multiplatform")
}

group = "dev.thibault"
version = "1.0.0-alpha01"
val mainFun = "dev.tclement.imgrecolor.main"
val mainClass = "dev.tclement.imgrecolor.MainKt"

repositories {
    mavenCentral()
}

kotlin {
    val nativeTargets = setOf(
        //macosX64(),
        linuxX64(),
        //mingwX64(),
    )

    jvm {
        compilations.getByName("main") {
            kotlinOptions.jvmTarget = "11"
        }
    }

    nativeTargets.forEach {
        it.compilations.getByName("main") {
            cinterops {
                val stbi by creating
                val lodepng by creating
                val curl by creating
            }
        }
        it.binaries {
            executable {
                entryPoint = mainFun
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(KotlinX.coroutines.core)
                implementation("com.github.ajalt.clikt:clikt:_")
                implementation("com.soywiz.korlibs.klock:klock:_")
                implementation("dev.kdrag0n:colorkt:_")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        if (nativeTargets.find { it.name == "linuxX64" } != null) {
            val linuxX64Main by getting {
                dependsOn(nativeMain)
            }
            val linuxX64Test by getting {
                dependsOn(nativeTest)
            }
        }
        if (nativeTargets.find { it.name == "mingwX64" } != null) {
            val mingwX64Main by getting {
                dependsOn(nativeMain)
            }
            val mingwX64Test by getting {
                dependsOn(nativeTest)
            }
        }
        if (nativeTargets.find { it.name == "macosX64" } != null) {
            val macosX64Main by getting {
                dependsOn(nativeMain)
            }
            val macosX64Test by getting {
                dependsOn(nativeTest)
            }
        }
    }
}

tasks {
    val clean by getting {
        doLast {
            remove("src/nativeInterop/cinterop/lodepng/build")
        }
    }

    val jvmJar by getting(Jar::class) {
        doFirst {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            val main by kotlin.jvm().compilations.getting
            manifest {
                attributes(
                    "Main-Class" to mainClass,
                )
            }
            from({
                main.runtimeDependencyFiles.files.map { if (it.isDirectory) it else zipTree(it) }
            })
        }
    }

    val copyLodepngFiles by creating(Copy::class) {
        inputs.files("src/nativeInterop/cinterop/lodepng/upstream")
        outputs.files("src/nativeInterop/cinterop/lodepng/lodepng.c", "src/nativeInterop/cinterop/lodepng/lodepng.h")
        rename {
            it.replace(".cpp", ".c")
        }
        from("src/nativeInterop/cinterop/lodepng/upstream")
        into("src/nativeInterop/cinterop/lodepng")
        include("lodepng.cpp", "lodepng.h")
    }

    val createLodepngDefFile by creating(Task::class) {
        inputs.files("\"src/nativeInterop/cinterop/lodepng/upstream")
        outputs.files("src/nativeInterop/cinterop/lodepng.def")
        file("src/nativeInterop/cinterop/lodepng.def").writeText("""
            package = lvandeve.lodepng
    
            headers = $rootDir/src/nativeInterop/cinterop/lodepng/lodepng.h
            staticLibraries = lodepng.a
            libraryPaths = $rootDir/src/nativeInterop/cinterop/lodepng/build/out/
            compilerOpts = -DLODEPNG_NO_COMPILE_DECODER
        """.trimIndent())
    }

    val buildLodepngStaticObject by creating(Exec::class) {
        dependsOn(copyLodepngFiles)
        inputs.dir("src/nativeInterop/cinterop/lodepng/upstream")
        outputs.dir("src/nativeInterop/cinterop/lodepng/build/obj")
        delete("src/nativeInterop/cinterop/lodepng/build/obj")
        mkdir("src/nativeInterop/cinterop/lodepng/build/obj/")
        commandLine("gcc", "-Wall", "-Werror", "-Wextra", "-c", "src/nativeInterop/cinterop/lodepng/lodepng.c", "-o", "src/nativeInterop/cinterop/lodepng/build/obj/lodepng.o")

    }

    val buildLodepngStaticLib by creating(Exec::class) {
        dependsOn(buildLodepngStaticObject)
        inputs.dir("src/nativeInterop/cinterop/lodepng/upstream")
        outputs.dir("src/nativeInterop/cinterop/lodepng/build/out")
        delete("src/nativeInterop/cinterop/lodepng/build/out")
        mkdir("src/nativeInterop/cinterop/lodepng/build/out/")
        commandLine("ar", "rcs", "src/nativeInterop/cinterop/lodepng/build/out/lodepng.a", "src/nativeInterop/cinterop/lodepng/build/obj/lodepng.o")
    }

    val createStbiDefFile by creating(Task::class) {
        file("src/nativeInterop/cinterop/stbi.def").writeText("""
            package = nothings.stbi
    
            headers = $rootDir/src/nativeInterop/cinterop/stb/stb_image.h
            compilerOpts = -DSTB_IMAGE_IMPLEMENTATION -DSTB_IMAGE_STATIC
        """.trimIndent())
    }

    withType(CInteropProcess::class) {
        when {
            name.contains("lodepng", ignoreCase = true) -> dependsOn(buildLodepngStaticLib, createLodepngDefFile)
            name.contains("stbi", ignoreCase = true) -> dependsOn(createStbiDefFile)
        }
    }
}

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

repositories.mavenCentral()

application.mainClass.set("BenchmarkKt")

dependencies { implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4") }

tasks.compileKotlin { kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.time.ExperimentalTime" }

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

repositories.mavenCentral()

application.mainClass.set("BenchmarkKt")

tasks.compileKotlin { kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.time.ExperimentalTime" }

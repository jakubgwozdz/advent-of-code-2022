package day3

import readAllText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    println(part1(readAllText("local/day3_input.txt")))
    println(part2(readAllText("local/day3_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

fun part1(input: String) = input.lineSequence().filter(String::isNotBlank)
    .map { it.take(it.length / 2).toSet() to it.takeLast(it.length / 2).toSet() }
    .map { (a, b) -> a.single { it in b } }
    .sumOf(::priority)

fun part2(input: String) = input.lineSequence().filter(String::isNotBlank)
    .chunked(3)
    .map { Triple(it[0].toSet(), it[1].toSet(), it[2].toSet()) }
    .map { (a, b, c) -> a.filter { it in b }.single { it in c } }
    .sumOf(::priority)

private fun priority(c: Char) = when {
    c.isLowerCase() -> c - 'a' + 1
    c.isUpperCase() -> c - 'A' + 27
    else -> error("WTF `$c`")
}

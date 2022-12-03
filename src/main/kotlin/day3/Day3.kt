package day3

import readAllText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    println(part1(readAllText("local/day3_input.txt")))
    println(part2(readAllText("local/day3_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

fun part1(input: String) = input.lineSequence().filter(String::isNotBlank)
    .map { it.chunked(it.length / 2) }
    .map { it.map(String::toSet).reduce(Set<Char>::intersect).single() }
    .sumOf(::priority)

fun part2(input: String) = input.lineSequence().filter(String::isNotBlank)
    .chunked(3)
    .map { it.map(String::toSet).reduce(Set<Char>::intersect).single() }
    .sumOf(::priority)

private fun priority(c: Char) = when {
    c.isLowerCase() -> c - 'a' + 1
    c.isUpperCase() -> c - 'A' + 27
    else -> error("WTF `$c`")
}

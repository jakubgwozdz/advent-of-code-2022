package day4

import parseRecords
import readAllText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    val example = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent()
    println(part2(example))
    println(part1(readAllText("local/day4_input.txt")))
    println(part2(readAllText("local/day4_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

private val regex = "(\\d+)-(\\d+),(\\d+)-(\\d+)".toRegex()
private fun buildRecord(matchResult: MatchResult) =
    matchResult.destructured.let { (a, b, c, d) ->
        (a.toInt()..b.toInt()) to (c.toInt()..d.toInt())
    }

fun part1(input: String) = input.parseRecords(regex, ::buildRecord)
    .count { (s1, s2) -> s1 isInside s2 || s2 isInside s1 }

private infix fun IntRange.isInside(other: IntRange) = first in other && last in other

fun part2(input: String) = input.parseRecords(regex, ::buildRecord)
    .count { (s1, s2) -> s1 overlaps s2 }

private infix fun IntRange.overlaps(other: IntRange) =
    first in other || last in other || other.first in this || other.last in this

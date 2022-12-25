package day4

import execute
import parseRecords
import readAllText

fun main() {
    val input = readAllText("local/day2_input.txt")
    val test = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent()
    execute(::part1, test, 157)
    execute(::part1, input)
    execute(::part2, test, 70)
    execute(::part2, input)
}

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

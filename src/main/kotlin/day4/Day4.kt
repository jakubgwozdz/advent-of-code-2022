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
private fun parse(matchResult: MatchResult) = matchResult.destructured
    .let { (a, b, c, d) -> listOf(a, b, c, d).map(String::toInt) }

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .count { (a, b, c, d) -> a in (c..d) && b in (c..d) || c in (a..b) && d in (a..b) }

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .count { (a, b, c, d) -> a in (c..d) || b in (c..d) || c in (a..b) || d in (a..b) }

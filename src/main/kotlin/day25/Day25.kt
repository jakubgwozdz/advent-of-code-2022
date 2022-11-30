package day25

import parseRecords
import readAllText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    println(part1(readAllText("local/day25_input.txt")))
    println(part2(readAllText("local/day25_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

private val regex = "(.+)".toRegex()
private fun parse(matchResult: MatchResult) = matchResult.destructured.let { (a) -> a }

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .count()

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .count()

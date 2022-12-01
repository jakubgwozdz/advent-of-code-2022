package day1

import readAllText
import splitBy
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    println(part1(readAllText("local/day1_input.txt")))
    println(part2(readAllText("local/day1_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

fun part1(input: String) = elves(input).max()
fun part2(input: String) = elves(input).sortedDescending().take(3).sum()

private fun elves(input: String) = input.lineSequence().splitBy(String::isBlank)
    .map { it.sumOf(String::toInt) }

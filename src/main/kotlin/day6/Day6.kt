package day6

import readAllText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    println(part1(readAllText("local/day6_input.txt")))
    println(part2(readAllText("local/day6_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

fun part1(input: String) = solve(input, 4)
fun part2(input: String) = solve(input, 14)

private fun solve(input: String, size: Int) =
    input.windowedSequence(size) { it.asIterable().distinct().size }
        .indexOfFirst { it == size } + size

package day6

import execute
import readAllText

fun main() {
    val input = readAllText("local/day6_input.txt")
    execute(::part1, input)
    execute(::part2, input)
}

fun part1(input: String) = solve(input, 4)
fun part2(input: String) = solve(input, 14)

private fun solve(input: String, size: Int) =
    input.windowedSequence(size) { it.asIterable().distinct().size }
        .indexOfFirst { it == size } + size

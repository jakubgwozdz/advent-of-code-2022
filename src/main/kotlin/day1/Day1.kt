package day1

import execute
import readAllText
import splitBy

fun part1(input: String) = elves(input).max()
fun part2(input: String) = elves(input).sortedDescending().take(3).sum()

private fun elves(input: String) = input.lineSequence().splitBy(String::isBlank)
    .map { it.sumOf(String::toInt) }

fun main() {
    val input = readAllText("local/day1_input.txt")
    val test = """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent()
    execute(::part1, test, 24000)
    execute(::part1, input)
    execute(::part2, test, 45000)
    execute(::part2, input)
}

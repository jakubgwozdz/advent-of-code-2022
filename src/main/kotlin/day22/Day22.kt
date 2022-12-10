package day22

import execute
import parseRecords
import readAllText

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .count()

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .count()

private val regex = "(.+)".toRegex()
private fun parse(matchResult: MatchResult) = matchResult.destructured.let { (a) -> a }

fun main() {
    val input = readAllText("local/day22_input.txt")
    val test = """
        
    """.trimIndent()
    execute(::part1, test)
    execute(::part1, input)
    execute(::part2, test)
    execute(::part2, input)
}

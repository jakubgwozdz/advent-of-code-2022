package day10

import parseRecords
import readAllText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    val test = """
        addx 15
        addx -11
        addx 6
        addx -3
        addx 5
        addx -1
        addx -8
        addx 13
        addx 4
        noop
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx -35
        addx 1
        addx 24
        addx -19
        addx 1
        addx 16
        addx -11
        noop
        noop
        addx 21
        addx -15
        noop
        noop
        addx -3
        addx 9
        addx 1
        addx -3
        addx 8
        addx 1
        addx 5
        noop
        noop
        noop
        noop
        noop
        addx -36
        noop
        addx 1
        addx 7
        noop
        noop
        noop
        addx 2
        addx 6
        noop
        noop
        noop
        noop
        noop
        addx 1
        noop
        noop
        addx 7
        addx 1
        noop
        addx -13
        addx 13
        addx 7
        noop
        addx 1
        addx -33
        noop
        noop
        noop
        addx 2
        noop
        noop
        noop
        addx 8
        noop
        addx -1
        addx 2
        addx 1
        noop
        addx 17
        addx -9
        addx 1
        addx 1
        addx -3
        addx 11
        noop
        noop
        addx 1
        noop
        addx 1
        noop
        noop
        addx -13
        addx -19
        addx 1
        addx 3
        addx 26
        addx -30
        addx 12
        addx -1
        addx 3
        addx 1
        noop
        noop
        noop
        addx -9
        addx 18
        addx 1
        addx 2
        noop
        noop
        addx 9
        noop
        noop
        noop
        addx -1
        addx 2
        addx -37
        addx 1
        addx 3
        noop
        addx 15
        addx -21
        addx 22
        addx -6
        addx 1
        noop
        addx 2
        addx 1
        noop
        addx -10
        noop
        noop
        addx 20
        addx 1
        addx 2
        addx 2
        addx -6
        addx -11
        noop
        noop
        noop
    """.trimIndent()

    println(part1(test))
//    TODO()
    println(part1(readAllText("local/day10_input.txt")))
    println(part2(readAllText("local/day10_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

private val regex = "(.+)".toRegex()
private fun parse(matchResult: MatchResult) = matchResult.destructured.let { (a) -> a }

fun part1(input: String): Long {
    val commands = input.lineSequence().filterNot(String::isBlank).toList()
    val result = mutableListOf<Pair<Int, Long>>()
    var cycle = 0
    var x = 1L
    commands.forEach { command ->
        cycle++
        if ((cycle +20) % 40 == 0) result += (cycle to x)
        if (command.startsWith("addx")) {
            cycle++
            if ((cycle +20) % 40 == 0) result += (cycle to x)
            x += command.substringAfter("addx ").toLong()
        }
    }
    return result.sumOf { (cycle, x) -> cycle * x }
}


fun part2(input: String) = input.parseRecords(regex, ::parse)
    .count()

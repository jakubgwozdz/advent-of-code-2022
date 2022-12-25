package day25

import execute
import readAllText
import wtf


fun part1(input: String) = input.lineSequence().filterNot(String::isBlank)
    .sumOf(::decode)
    .let(::encode)

private fun decode(line: String) = line.fold(0L) { acc, c ->
    acc * 5 + when (c) {
        '=' -> -2
        '-' -> -1
        '0' -> 0
        '1' -> 1
        '2' -> 2
        else -> wtf(c)
    }
}

private fun encode(number: Long): String {
    if (number == 0L) return "0"
    var b = number
    return buildString {
        while (b > 0) {
            val m = (b + 2) % 5
            b = (b + 2) / 5
            append("=-012"[m.toInt()])
        }
    }.reversed()
}

fun part2(input: String) = 0


fun main() {
    val input = readAllText("local/day25_input.txt")
    val test = """
        1=-0-2
        12111
        2=0=
        21
        2=01
        111
        20012
        112
        1=-1=
        1-12
        12
        1=
        122
    """.trimIndent()
    execute(::part1, test, "2=-1=0")
    execute(::part1, input)
}

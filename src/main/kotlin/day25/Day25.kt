package day25

import execute
import readAllText


fun part1(input: String) = input.lineSequence()
    .sumOf(::decode)
    .let(::encode)

private const val DIGITS = "=-012"

private fun decode(line: String) = line.fold(0L) { acc, c ->
    acc * 5 + DIGITS.indexOf(c) - 2
}

private fun encode(number: Long) = buildString {
    var b = number
    while (b > 0) {
        append(DIGITS[((b + 2) % 5).toInt()])
        b = (b + 2) / 5
    }
}.reversed().ifEmpty { "0" }

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
    execute(::part1, input, "122-0==-=211==-2-200")
}

package day3

import execute
import readAllText
import wtf

fun part1(input: String) = input.lineSequence().filter(String::isNotBlank)
    .map { it.chunked(it.length / 2).map(String::toSet) }
    .map { (p1, p2) -> p1.first { it in p2 } }
    .sumOf(::priority)

fun part2(input: String) = input.lineSequence().filter(String::isNotBlank)
    .chunked(3).map { it.map(String::toSet) }
    .map { (p1, p2, p3) -> p1.first { it in p2 && it in p3 } }
    .sumOf(::priority)

private fun priority(c: Char) = when {
    c.isLowerCase() -> c - 'a' + 1
    c.isUpperCase() -> c - 'A' + 27
    else -> wtf(c)
}

fun main() {
    val input = readAllText("local/day3_input.txt")
    val test = """
        vJrwpWtwJgWrhcsFMMfFFhFp
        jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
        PmmdzqPrVvPwwTWBwg
        wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
        ttgJtRGJQctTZtZT
        CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent()
    execute(::part1, test, 157)
    execute(::part1, input)
    execute(::part2, test, 70)
    execute(::part2, input)
}

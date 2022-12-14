package day14

import execute
import readAllText
import wtf

typealias Cave = MutableSet<Pair<Int, Int>>

private fun Cave.abyssLevel() = maxOf { it.second } + 2

fun part1(input: String) = input
    .let(::buildData)
    .let(::simulate)

fun part2(input: String) = input
    .let(::buildData)
    .also { data ->
        val bottom = data.abyssLevel()
        (500 - bottom - 1..500 + bottom + 1).forEach { data += it to bottom }
    }
    .let(::simulate)

private fun simulate(data: Cave): Int {
    val start = 500 to 0
    val abyssLevel = data.abyssLevel()
    var count = 0
    var abyss = false
    while (start !in data && !abyss) {
        var (x, y) = start
        var rest = false
        while (!rest && !abyss) when {
            y > abyssLevel -> abyss = true

            x to y + 1 !in data -> y += 1

            x - 1 to y + 1 !in data -> {
                x -= 1; y += 1
            }

            x + 1 to y + 1 !in data -> {
                x += 1; y += 1
            }

            else -> rest = true
        }
        if (!abyss) {
            data += x to y
            count++
        }
    }
    return count
}

private fun buildData(input: String): Cave = input.lineSequence().filterNot(String::isBlank)
    .map { it.split(" -> ").map { s -> s.split(",").let { (x, y) -> (x.toInt() to y.toInt()) } } }
    .fold(mutableSetOf<Pair<Int, Int>>()) { acc, lines ->
        lines.windowed(2).map { (s, e) ->
            val (sx, sy) = s
            val (ex, ey) = e
            when {
                sx < ex -> (sx..ex).map { it to ey }
                sx > ex -> (sx downTo ex).map { it to ey }
                sy < ey -> (sy..ey).map { ex to it }
                sy > ey -> (sy downTo ey).map { ex to it }
                else -> wtf(s to e)
            }
        }.forEach { range ->
            acc.addAll(range)
        }
        acc
    }

fun main() {
    val input = readAllText("local/day14_input.txt")
    val test = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent()
    execute(::part1, test, 24)
    execute(::part1, input, 578)
    execute(::part2, test, 93)
    execute(::part2, input, 24377)
}

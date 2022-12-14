package day14

import execute
import readAllText
import wtf

fun part1(input: String) = buildData(input)
    .let { data ->
        val ey = data.maxOf { it.second } + 2
        simulate(data, ey)
    }

fun part2(input: String) = buildData(input)
    .let { data ->
        val ey = data.maxOf { it.second } + 2
        (500 - ey - 1..500 + ey + 1).forEach { data += it to ey }
        simulate(data, ey)
    }

private fun simulate(data: MutableSet<Pair<Int, Int>>, abyssLevel: Int): Int {
    val start = 500 to 0
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

private fun buildData(input: String) = input.lineSequence().filterNot(String::isBlank)
    .map { it.split(" -> ").map { s -> s.split(",").let { (x, y) -> (x.toInt() to y.toInt()) } } }
    .fold(mutableSetOf<Pair<Int, Int>>()) { acc, lines ->
        lines.windowed(2).forEach { (s, e) ->
            val (sx, sy) = s
            val (ex, ey) = e
            val range = if (sx < ex) (sx..ex).map { it to ey }
            else if (sx > ex) (sx downTo ex).map { it to ey }
            else if (sy < ey) (sy..ey).map { ex to it }
            else if (sy > ey) (sy downTo ey).map { ex to it }
            else wtf(s to e)
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

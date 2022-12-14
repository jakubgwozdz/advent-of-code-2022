package day14

import Stack
import execute
import readAllText
import wtf

class Cave(val abyss: Int) {
    private val data: Array<BooleanArray> = Array(abyss + 2) { BooleanArray(abyss * 2 + 6) }
    private val offset: Int = 500 - abyss - 3

    operator fun plusAssign(point: Pair<Int, Int>) {
        val (x, y) = point
        data[y][x - offset] = true
    }

    operator fun contains(point: Pair<Int, Int>): Boolean {
        val (x, y) = point
        return y in data.indices && (x - offset) in data[y].indices && data[y][x - offset]
    }
}

fun part1(input: String) = input
    .let(::buildData)
    .let(::simulate)

fun part2(input: String) = input
    .let(::buildData)
    .also { data ->
        val bottom = data.abyss
        (500 - bottom - 1..500 + bottom + 1).forEach { data += it to bottom }
    }
    .let(::simulate)

private fun simulate(data: Cave): Int {
    val starts = Stack<Pair<Int, Int>>().apply { offer(500 to 0) }
    var count = 0
    var abyssHit = false
    while (starts.isNotEmpty() && !abyssHit) {
        var (x, y) = starts.poll()
        var rest = false
        while (!rest && !abyssHit) when {
            y > data.abyss -> abyssHit = true

            x to y + 1 !in data -> {
                starts.offer(x to y)
                y += 1
            }

            x - 1 to y + 1 !in data -> {
                starts.offer(x to y)
                x -= 1; y += 1
            }

            x + 1 to y + 1 !in data -> {
                starts.offer(x to y)
                x += 1; y += 1
            }

            else -> rest = true
        }
        if (!abyssHit) {
            data += x to y
            count++
        }
    }
    return count
}

private fun buildData(input: String): Cave = input.lineSequence().filterNot(String::isBlank)
    .map { it.split(" -> ").map { s -> s.split(",").let { (x, y) -> (x.toInt() to y.toInt()) } } }
    .flatMap { it.windowed(2) }
    .map { (s, e) -> s to e }
    .toList()
    .let { lines ->
        val abyss = lines.maxOf { maxOf(it.first.second, it.second.second) } + 2
        val result = Cave(abyss)
        lines.forEach { (s, e) ->
            val (sx, sy) = s
            val (ex, ey) = e
            when {
                sx < ex -> (sx..ex).map { it to ey }
                sx > ex -> (sx downTo ex).map { it to ey }
                sy < ey -> (sy..ey).map { ex to it }
                sy > ey -> (sy downTo ey).map { ex to it }
                else -> wtf(s to e)
            }.forEach { p -> result += p }
        }
        result
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

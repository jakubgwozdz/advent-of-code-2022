package day20

import execute
import readAllText

private class Elem(val v: Long) {
    override fun toString() = v.toString()
}

private fun List<Elem>.process(e: Elem): List<Elem> {
    val oldIdx = withIndex().single { it.value == e }.index
    val newResult = subList(oldIdx + 1, size) + subList(0, oldIdx) + subList(oldIdx + 1, size) + subList(0, oldIdx)

    var delta = (e.v % (size - 1)).toInt()
//    while (delta >= 0) delta -= size - 1
    if (delta < 0) delta += size - 1

    return newResult.subList(delta, delta + size - 1) + e
}

fun part1(input: String) = solve(input, 1, 1)
fun part2(input: String) = solve(input, 811589153, 10)

private fun solve(input: String, multiplier: Long, times: Int) = input.lineSequence().mapNotNull { it.toIntOrNull() }
    .toList()
    .map { Elem(it * multiplier) }
    .let { initial ->
        var result = initial
        repeat(times) { initial.forEach { result = result.process(it) } }

        val offset = result.withIndex().single { it.value.v == 0L }.index
        listOf(1000, 2000, 3000).sumOf { result[(offset + it) % result.size].v }
    }

fun main() {
    val input = readAllText("local/day20_input.txt")
    val test = """
        1
        2
        -3
        3
        -2
        0
        4
    """.trimIndent()

    execute(::part1, test, 3)
    execute(::part1, input, 8764)
    execute(::part2, test, 1623178306)
    execute(::part2, input, 535648840980)
}

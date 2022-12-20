package day20

import execute
import readAllText

fun part1(input: String) = parse(input)
    .let { initial ->
        var result = initial
        initial.forEach { n ->
            result = result.process(n)
        }

        result
    }
    .let {
        val offset = it.indexOf(0)
        Triple(it[(offset + 1000) % it.size], it[(offset + 2000) % it.size], it[(offset + 3000) % it.size])
    }
    .also { println(it) }
    .let { (a, b, c) -> a + b + c }

private fun parse(input: String) = input.lineSequence().mapNotNull { it.toIntOrNull() }
    .toList()

private fun List<Int>.process(n: Int): List<Int> = if (n % (size - 1) != 0) {
    val oldIdx = indexOf(n)
    var newIdx = oldIdx + n
    while (newIdx < 0) newIdx += size - 1
    while (newIdx >= size) newIdx -= size - 1
    val newResult = if (newIdx > oldIdx) {
        val start = subList(0, oldIdx)
        val mid = subList(oldIdx + 1, newIdx + 1)
        val end = subList(newIdx + 1, size)
        start + mid + n + end
    } else {
        val start = subList(0, newIdx)
        val mid = subList(newIdx, oldIdx)
        val end = subList(oldIdx + 1, size)
        start + n + mid + end
    }
//                println("$n: $oldIdx->$newIdx : $result -> $newResult")
    newResult
} else this

fun part2(input: String) = parse(input)
    .count()

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

    println(listOf(0, -22, 2, 3, 4, 5, 6, 7, 8, 9).process(-22))

    execute(::part1, test, 3)
    execute(::part1, input)
    execute(::part2, test)
    execute(::part2, input)
}

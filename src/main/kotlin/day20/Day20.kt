package day20

import execute
import readAllText

private class Elem(val v: Long) {
    override fun toString() = v.toString()
}

fun part1(input: String) = parse(input)
    .map { Elem(it * 1L) }
    .let { initial ->
        var result = initial
        initial.forEach {
            result = result.process(it)
        }

        val offset = result.withIndex().single { it.value.v == 0L }.index
        val first = (offset + 1000) % result.size
        val second = (offset + 2000) % result.size
        val third = (offset + 3000) % result.size
        Triple(result[first], result[second], result[third])
    }
    .also { println(it) }
    .let { (a, b, c) -> a.v + b.v + c.v }


private fun parse(input: String) = input.lineSequence().mapNotNull { it.toIntOrNull() }
    .toList()

private fun List<Elem>.process(e: Elem): List<Elem> {
    val oldIdx = withIndex().single { it.value == e }.index
    val newResult = subList(oldIdx + 1, size) + subList(0, oldIdx) + subList(oldIdx + 1, size) + subList(0, oldIdx)

    var delta = (e.v % (size - 1)).toInt()
    while (delta >= 0) delta -= size - 1
    while (delta < 0) delta += size - 1

    return newResult.subList(delta, delta + size - 1) + e
}

fun part2(input: String) = parse(input)
    .map { Elem(it * 811589153L) }
    .let { initial ->
        var result = initial
        repeat(10) {
            initial.forEach {
                result = result.process(it)
            }
        }

        val offset = result.withIndex().single { it.value.v == 0L }.index
        val first = (offset + 1000) % result.size
        val second = (offset + 2000) % result.size
        val third = (offset + 3000) % result.size
        Triple(result[first], result[second], result[third])
    }
    .also { println(it) }
    .let { (a, b, c) -> a.v + b.v + c.v }


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

//    println(listOf(0, -22, 2, 3, 4, 5, 6, 7, 8, 9).process(-22))

    execute(::part1, test, 3)
    execute(::part1, input, 8764)
    execute(::part2, test, 1623178306)
    execute(::part2, input)
}

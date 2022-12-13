package day13

import execute
import readAllText

sealed class Node : Comparable<Node>

data class NumberNode(val value: Int) : Node() {
    override fun compareTo(other: Node): Int = when (other) {
        is ListNode -> ListNode(listOf(this)).compareTo(other)
        is NumberNode -> value.compareTo(other.value)
    }
}

data class ListNode(val value: List<Node>) : Node() {
    override fun compareTo(other: Node): Int {
        when (other) {
            is NumberNode -> return compareTo(ListNode(listOf(other)))
            is ListNode -> {
                for (p in value.indices) {
                    if (p in other.value.indices) {
                        val r = value[p].compareTo(other.value[p])
                        if (r != 0) return r
                    }
                }
                return value.size.compareTo(other.value.size)
            }
        }
    }
}

fun part1(input: String) = input.lineSequence().filterNot(String::isBlank)
    .map(::parse)
    .chunked(2)
    .mapIndexed { index, (left, right) ->
        if (left < right) index + 1 else 0
    }
    .sum()

private val separators by lazy { listOf("[[2]]", "[[6]]").map { parse(it) } }

fun part2(input: String) = input.lineSequence()
    .filterNot(String::isBlank)
    .map(::parse)
    .toList()
    .let { it + separators }
    .sorted()
    .let { ordered -> separators.map { ordered.indexOf(it) + 1 }.reduce(Int::times) }

private fun parse(str: String): Node =
    if (str.startsWith("["))
        ListNode(parseList(str).map { parse(it) })
    else
        NumberNode(str.toInt())

private fun parseList(str: String) = buildList {
    var p = 0
    var q = 0
    var i = 0
    while (q in str.indices) {
        if (str[q] == '[') i++
        if (str[q] == ']') i--
        if (str[q] == ',' && i == 1) {
            add(str.substring(p + 1, q))
            p = q
        }
        q++
    }
    if (p + 1 != q - 1) add(str.substring(p + 1, q - 1))
}

fun main() {
    val input = readAllText("local/day13_input.txt")
    val test = """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent()
    execute(::part1, test, 13)
    execute(::part1, input, 5720)
    execute(::part2, test, 140)
    execute(::part2, input, 23504)
}

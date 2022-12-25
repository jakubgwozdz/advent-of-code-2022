package day8

import execute
import readAllText
import kotlin.math.absoluteValue

fun main() {
    val test = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()
    val input = readAllText("local/day8_input.txt")
    execute(::part1, test)
    execute(::part1, input)
    execute(::part2, test)
    execute(::part2, input)
}

fun part1(input: String) = input.lineSequence().filterNot(String::isBlank).toList().let { rows ->
    rows.indices.sumOf { y ->
        rows.first().indices.count { x ->
            val c = rows[y][x]
            val u = (y - 1 downTo 0).all { i -> rows[i][x] < c }
            val d = (y + 1..rows.lastIndex).all { i -> rows[i][x] < c }
            val l = (x - 1 downTo 0).all { i -> rows[y][i] < c }
            val r = (x + 1..rows.first().lastIndex).all { i -> rows[y][i] < c }
            u || d || l || r
        }
    }
}

fun IntProgression.distanceTo(op: (Int) -> Boolean): Int = asSequence().takeWhile { !op(it) }.lastOrNull()
    ?.let { (it - first).absoluteValue + if (it == last) 1 else 2 } ?: 0

fun part2(input: String) = input.lines().filterNot(String::isBlank).let { rows ->
    rows.indices.maxOf { y ->
        rows.first().indices.maxOf { x ->
            val c = rows[y][x]
            val u = (y - 1 downTo 0).distanceTo { i -> rows[i][x] >= c }
            val d = (y + 1..rows.lastIndex).distanceTo { i -> rows[i][x] >= c }
            val l = (x - 1 downTo 0).distanceTo { i -> rows[y][i] >= c }
            val r = (x + 1..rows.first().lastIndex).distanceTo { i -> rows[y][i] >= c }
            u * d * l * r
        }
    }
}

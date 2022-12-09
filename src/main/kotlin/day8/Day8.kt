package day8

import readAllText
import kotlin.math.absoluteValue
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    val test = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()
    println(part1(test))
    println(part1(readAllText("local/day8_input.txt")))
    println(part2(test))
    println(part2(readAllText("local/day8_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

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

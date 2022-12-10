package day9

import execute
import readAllText
import kotlin.math.sign

enum class Direction { U, D, L, R }
data class Pos(val r: Int, val c: Int) {
    fun isAdjacent(other: Pos) = (r - other.r) in (-1..1) && (c - other.c) in (-1..1)
    fun moveTowards(other: Pos) = Pos(r + (other.r - r).sign, c + (other.c - c).sign)
    fun move(d: Direction) = when (d) {
        Direction.U -> copy(r = r - 1)
        Direction.D -> copy(r = r + 1)
        Direction.L -> copy(c = c - 1)
        Direction.R -> copy(c = c + 1)
    }
}

class State(length: Int) {
    var rope: List<Pos> = (1..length).map { Pos(0, 0) }
    val visited: MutableSet<Pos> = mutableSetOf(Pos(0, 0))

    fun updateWith(newRope: List<Pos>) {
        rope = newRope
        visited += newRope.last()
    }

}

fun State.solve(input: String) = moveSequence(input)
    .flatMap { (direction, l) -> (1..l).map { direction } }
    .forEach { direction ->
        buildList {
            rope.forEachIndexed { index, pos ->
                add(
                    when {
                        index == 0 -> pos.move(direction)
                        pos.isAdjacent(last()) -> pos
                        else -> pos.moveTowards(last())
                    }
                )
            }
        }.let { updateWith(it) }
    }

fun part1(input: String) = State(2).apply { solve(input) }.visited.size
fun part2(input: String) = State(10).apply { solve(input) }.visited.size

fun State.printIt() {
    val all = visited + rope.toSet()
    val rows = all.minOf { it.r } - 1..all.maxOf { it.r } + 1
    val cols = all.minOf { it.c } - 1..all.maxOf { it.c } + 1
    rows.forEach { r ->
        println(cols.joinToString("") { c ->
            when (val p = Pos(r, c)) {
                rope.first() -> "H"
                in rope -> rope.indexOf(p).toString()
                in visited -> "s"
                else -> "."
            }
        })
    }
    println()
}

private val regex = "(.) (\\d+)".toRegex()
private fun moveSequence(input: String) = input.lineSequence()
    .filterNot(String::isBlank)
    .map { regex.matchEntire(it) ?: error("WTF `$it`") }
    .map { it.destructured.let { (a, b) -> Direction.valueOf(a) to b.toInt() } }

fun main() {
    val input = readAllText("local/day9_input.txt")
    val test = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent()
    val test2 = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent()
    execute(::part1, test, 13)
    execute(::part1, input)
    execute(::part2, test, 1)
    execute(::part2, test2, 36)
    execute(::part2, input)
}

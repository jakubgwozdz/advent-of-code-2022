package day9

import parseRecords
import readAllText
import kotlin.math.sign
import kotlin.time.DurationUnit
import kotlin.time.measureTime

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

data class State(
    val rope: List<Pos>,
    val visited: Set<Pos>,
) {
    constructor(length: Int) : this((1..length).map { Pos(0, 0) }, setOf(Pos(0, 0)))
}

fun part1(input: String) = solve(input, 2).visited.size

fun part2(input: String) = solve(input, 10).visited.size

private fun solve(input: String, length: Int) = input.parseRecords(regex, ::parse)
    .flatMap { (d, l) -> (1..l).map { d } }
    .fold(State(length)) { (rope, visited), d ->
        val newRope = buildList(length) {
            var last = rope.first().move(d)
            add(last)
            rope.drop(1).forEach { pos ->
                val prev = last
                last = if (pos.isAdjacent(prev)) pos else pos.moveTowards(prev)
                add(last)
            }
        }
        State(newRope, visited + newRope.last())
    }


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
private fun parse(matchResult: MatchResult) =
    matchResult.destructured.let { (a, b) -> Direction.valueOf(a) to b.toInt() }

fun main() = measureTime {
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
    println(part1(test))
    println(part2(test))
    println(part2(test2))
    val input = readAllText("local/day9_input.txt")
    println(part1(input))
    println(part2(input))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

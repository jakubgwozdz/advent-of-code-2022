package day9

import parseRecords
import readAllText
import kotlin.math.absoluteValue
import kotlin.time.DurationUnit
import kotlin.time.measureTime

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
    println(part1(test))
    println(part2(test))
    println(part1(readAllText("local/day9_input.txt")))
    println(part2(readAllText("local/day9_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

private val regex = "(.) (\\d+)".toRegex()
private fun parse(matchResult: MatchResult) =
    matchResult.destructured.let { (a, b) -> Direction.valueOf(a) to b.toInt() }

enum class Direction { U, D, L, R }
data class Pos(val r: Int, val c: Int)

data class State(
    val head: Pos = Pos(0, 0),
    val tail: Pos = head,
    val visited: Set<Pos> = setOf(tail),
)

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .flatMap { (d, l) -> (1..l).map { d to 1 } }
    .fold(State()) { acc, (d, l) ->
        val newHead = when (d) {
            Direction.U -> acc.head.run { copy(r = r + 1) }
            Direction.D -> acc.head.run { copy(r = r - 1) }
            Direction.L -> acc.head.run { copy(c = c - 1) }
            Direction.R -> acc.head.run { copy(c = c + 1) }
        }
        val newTail = if ((newHead.r - acc.tail.r) in (-1..1) && (newHead.c - acc.tail.c) in (-1..1))
            acc.tail else acc.head
        State(newHead, newTail, acc.visited+newTail)
    }
    .visited.size

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .count()

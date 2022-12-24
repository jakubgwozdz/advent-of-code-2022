package day24

import Queue
import execute
import readAllText
import kotlin.math.absoluteValue

typealias Pos = Pair<Int, Int>
typealias Blizzard = Pair<Pos, Dir>

enum class Dir(val v: Pos) {
    N(-1 to 0), W(0 to -1),
    E(0 to 1), S(1 to 0),
}

operator fun Pos.plus(o: Pos) = first + o.first to second + o.second
operator fun Pos.plus(o: Dir) = this + o.v

private fun Int.keepIn(intRange: IntRange): Int = when {
    this > intRange.last -> intRange.first
    this < intRange.first -> intRange.last
    else -> this
}

private fun Pos.manhattan(dest: Pos) = (first - dest.first).absoluteValue + (second - dest.second).absoluteValue

data class Valley(
    val rows: IntRange,
    val cols: IntRange,
    val enter: Pos,
    val exit: Pos,
    val blizzards: List<Blizzard>,
) {
    val lcm = with(rows.count() to cols.count()) { // hackaton style
        when (this) {
            (4 to 6) -> 12 // test input
            (25 to 120) -> 600 // real input
            else -> TODO(this.toString())
        }
    }
    private val blizzardsByTime: Map<Int, Set<Pos>> = buildMap {
        var s = blizzards
        repeat(lcm) { time ->
            put(time, s.map { it.first }.toHashSet())
            s = s.map { (p, dir) ->
                val r = (p.first + dir.v.first).keepIn(rows)
                val c = (p.second + dir.v.second).keepIn(cols)
                Blizzard(r to c, dir)
            }
        }
    }

    fun blizzardsAt(time: Int) = blizzardsByTime[time % lcm]!!
}

data class State(val pos: Pos, val timeElapsed: Int = 0)

fun State.trimmed(valley: Valley) = copy(timeElapsed = timeElapsed % valley.lcm)

fun State.goTo(valley: Valley, exit: Pos): State {
    val queue = Queue(this)
    val visited = mutableSetOf<State>()
    while (true) {
        val state = queue.poll()
        val trimmed = state.trimmed(valley)
        if (trimmed !in visited)
            state.next(valley)
                .forEach {
                    if (it.pos == exit) return it
                    if (it.trimmed(valley) !in visited) queue.offer(it)
                }
                .also { visited += trimmed }
    }
}

fun part1(input: String): Int {
    val valley = parseValley(input)
    return State(valley.enter)
        .goTo(valley, valley.exit)
        .timeElapsed
}

fun part2(input: String): Int {
    val valley = parseValley(input)
    return State(valley.enter)
        .goTo(valley, valley.exit)
        .goTo(valley, valley.enter)
        .goTo(valley, valley.exit)
        .timeElapsed
}


private fun State.next(valley: Valley): List<State> {
    val t = timeElapsed + 1
    val occupied = valley.blizzardsAt(t)
    val possibleMoves = (Dir.values().map { pos + it } + pos)
        .filter { p: Pos ->
            (p.first in valley.rows && p.second in valley.cols || p == valley.exit || p == valley.enter)
                    && p !in occupied
        }
    return possibleMoves.map { State(it, t) }
        .sortedBy { it.pos.manhattan(valley.exit) }
}

private fun parseValley(input: String): Valley {
    val lines = input.trimEnd().lines()
    val enter = 0 to lines.first().indexOf('.')
    val exit = lines.indices.last to lines.last().indexOf('.')
    val rows = 1 until lines.indices.last
    val cols = 1 until lines.first().indices.last
    val blizzards = buildList {
        rows.forEach { r ->
            cols.forEach { c ->
                when (lines[r][c]) {
                    '^' -> add(Blizzard(r to c, Dir.N))
                    'v' -> add(Blizzard(r to c, Dir.S))
                    '<' -> add(Blizzard(r to c, Dir.W))
                    '>' -> add(Blizzard(r to c, Dir.E))
                }
            }
        }
    }
    return Valley(rows, cols, enter, exit, blizzards)
}


fun main() {
    val input = readAllText("local/day24_input.txt")
    val test = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent()
    execute(::part1, test, 18)
    execute(::part1, input, 264)
    execute(::part2, test, 54)
    execute(::part2, input)
}

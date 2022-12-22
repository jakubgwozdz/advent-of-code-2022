package day22

import execute
import readAllText
import wtf

typealias Point = Pair<Int, Int> // (row, column) 1-based indexes

data class Board(val rows: Map<Int, IntRange>, val cols: Map<Int, IntRange>, val walls: Set<Point>) {
    val faceSize by lazy { if (rows.keys.any { it > 50 }) 50 else 4 }
}

sealed interface Op
data class Move(val steps: Int) : Op
enum class Turn : Op { L, R }

fun part1(input: String) = input.split("\n\n")
    .let { (boardData, pathData) -> parseBoard(boardData, pathData) }
    .let { (board, path) -> path.fold(Point(1, 1) to 0, board::playPart1) }
    .let { (pos, dir) -> pos.first * 1000 + pos.second * 4 + dir }

fun part2(input: String) = input.split("\n\n")
    .let { (boardData, pathData) -> parseBoard(boardData, pathData) }
    .let { (board, path) -> path.fold(Point(1, 1) to 0, board::playPart2) }
    .let { (pos, dir) -> pos.first * 1000 + pos.second * 4 + dir }

private fun Board.playPart1(from: Pair<Point, Int>, op: Op): Pair<Point, Int> {
    var (pos1, dir1) = from
    when (op) {
        Turn.L -> dir1 = (dir1 + 3) % 4
        Turn.R -> dir1 = (dir1 + 1) % 4
        else -> repeat((op as Move).steps) {
            val (r, c) = pos1
            val posX = when (dir1) {
                0 -> r to (c + 1).keepIn(rows[r]!!)
                1 -> (r + 1).keepIn(cols[c]!!) to c
                2 -> r to (c - 1).keepIn(rows[r]!!)
                3 -> (r - 1).keepIn(cols[c]!!) to c
                else -> wtf(dir1)
            }
            if (posX !in walls) pos1 = posX
        }
    }
    return pos1 to dir1
}

private fun Board.playPart2(from: Pair<Point, Int>, op: Op): Pair<Point, Int> {
    TODO()
}

private fun parseBoard(
    boardData: String,
    pathData: String
): Pair<Board, List<Op>> {
    val lines = boardData.lines()
    val walls = buildSet {
        lines.forEachIndexed { r, line ->
            line.forEachIndexed { c, p -> if (p == '#') add(Point(r + 1, c + 1)) }
        }
    }
    val rows =
        lines.mapIndexed { r0, line -> r0 + 1 to line.indexOfFirst { it != ' ' } + 1..line.indexOfLast { it != ' ' } + 1 }
            .filterNot { (_, r) -> r.isEmpty() }
            .toMap()
    val cols = (0..lines.maxOf { it.length }).map { c0 ->
        c0 + 1 to
                lines.indexOfFirst { line -> c0 in line.indices && line[c0] != ' ' } + 1..
                lines.indexOfLast { line -> c0 in line.indices && line[c0] != ' ' } + 1
    }
        .filterNot { (_, r) -> r.isEmpty() }
        .toMap()
    val board = Board(rows, cols, walls)
    val path = buildList<Op> {
        pathData.trimEnd().forEach {
            if (it.isDigit() && this.lastOrNull() is Move) add(Move((removeLast() as Move).steps * 10 + it.digitToInt()))
            else add(
                when {
                    it.isDigit() -> Move(it.digitToInt())
                    it == 'L' -> Turn.L
                    it == 'R' -> Turn.R
                    else -> wtf(it)
                }
            )
        }

    }

    return board to path
}

private fun Int.keepIn(intRange: IntRange): Int = when {
    this > intRange.last -> intRange.first
    this < intRange.first -> intRange.last
    else -> this
}

fun main() {
    val input = readAllText("local/day22_input.txt")
    val test = """
                ...#
                .#..
                #...
                ....
        ...#.......#
        ........#...
        ..#....#....
        ..........#.
                ...#....
                .....#..
                .#......
                ......#.

        10R5L5R10L4R5L5
    """.trimIndent()
    execute(::part1, test, 6032)
    execute(::part1, input, 66292)
    execute(::part2, test, 5031)
    execute(::part2, input)
}

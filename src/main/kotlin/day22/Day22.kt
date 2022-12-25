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
    .let { (board, path) -> path.fold(Point(1, board.rows[1]!!.first) to 0, board::playPart1) }
    .let { (pos, dir) -> pos.first * 1000 + pos.second * 4 + dir }

fun part2(input: String) = input.split("\n\n")
    .let { (boardData, pathData) -> parseBoard(boardData, pathData) }
    .let { (board, path) -> path.fold(Point(1, board.rows[1]!!.first) to 0, board::playPart2) }
    .let { (pos, dir) -> pos.first * 1000 + pos.second * 4 + dir }

private fun Board.playPart1(from: Pair<Point, Dir>, op: Op) =
    play(from, op, ::stepPart1)

private fun Board.playPart2(from: Pair<Point, Dir>, op: Op) =
    play(from, op, ::stepPart2)

private fun Board.play(
    from: Pair<Point, Dir>,
    op: Op,
    step: (Pair<Point, Dir>) -> Pair<Point, Dir>
): Pair<Point, Dir> = when (op) {
    Turn.L -> from.first to (from.second + 3) % 4
    Turn.R -> from.first to (from.second + 1) % 4
    is Move -> (1..op.steps).fold(from) { acc, _ ->
        val fromX = step(acc)
        if (fromX.first !in walls) fromX else acc
    }
}

private fun Board.stepPart1(from: Pair<Point, Dir>): Pair<Point, Dir> = from.let { (pos, dir) ->
    val (r, c) = pos
    when (dir) {
        0 -> r to (c + 1).keepIn(rows[r]!!)
        1 -> (r + 1).keepIn(cols[c]!!) to c
        2 -> r to (c - 1).keepIn(rows[r]!!)
        3 -> (r - 1).keepIn(cols[c]!!) to c
        else -> wtf(dir)
    } to dir
}

typealias Block = Pair<Int, Int>
typealias Dir = Int

private val transitions4 = buildMap<Pair<Block, Dir>, (Pair<Point, Dir>) -> Pair<Point, Dir>> {
    val faceSize = 4
    this[0 to 2 to 1] = { it }
    this[1 to 2 to 3] = { it }
    this[1 to 0 to 2] = { it }
    this[1 to 1 to 0] = { it }
    this[1 to 1 to 2] = { it }
    this[1 to 2 to 0] = { it }
    this[1 to 2 to 1] = { it }
    this[2 to 2 to 3] = { it }
    this[2 to 2 to 2] = { it }
    this[2 to 3 to 0] = { it }
    this[1 to 3 to 0] = { right(it, pivot10BL(faceSize, 1 to 3)) }
    this[3 to 2 to 1] = { right(it, pivot10TR(faceSize, 2 to 1)).let { right(it, pivot10TR(faceSize, 2 to 0)) } }
    this[0 to 1 to 3] = { left(it, pivot10BR(faceSize, 0 to 1)) }
}

private val transitions50 = buildMap<Pair<Block, Dir>, (Pair<Point, Dir>) -> Pair<Point, Dir>> {
    val faceSize = 50
    this[0 to 1 to 2] = { it }
    this[0 to 2 to 0] = { it }
    this[0 to 1 to 3] = { it }
    this[1 to 1 to 1] = { it }
    this[1 to 1 to 3] = { it }
    this[2 to 1 to 1] = { it }
    this[2 to 1 to 0] = { it }
    this[2 to 0 to 2] = { it }
    this[2 to 0 to 3] = { it }
    this[3 to 0 to 1] = { it }

    this[3 to 1 to 1] = { right(it, pivot10TL(faceSize, 3 to 1)) }
    this[3 to 1 to 0] = { left(it, pivot10TL(faceSize, 3 to 1)) }

    this[1 to 2 to 1] = { right(it, pivot10TL(faceSize, 1 to 2)) }
    this[1 to 2 to 0] = { left(it, pivot10TL(faceSize, 1 to 2)) }

    this[1 to 0 to 2] = { left(it, pivot10BR(faceSize, 1 to 0)) }
    this[1 to 0 to 3] = { right(it, pivot10BR(faceSize, 1 to 0)) }

    this[0 to 3 to 0] = { right(it, pivot10TL(faceSize, 1 to 2)).let { right(it, pivot10TL(faceSize, 2 to 2)) } }
    this[2 to 2 to 0] = { left(it, pivot10TL(faceSize, 1 to 2)).let { left(it, pivot10BL(faceSize, 0 to 3)) } }

    this[0 to 0 to 2] = { left(it, pivot10BR(faceSize, 1 to 0)).let { left(it, pivot10TR(faceSize, 2 to -1)) } }
    this[2 to -1 to 2] = { right(it, pivot10BR(faceSize, 1 to 0)).let { right(it, pivot10BR(faceSize, 0 to 0)) } }

    this[4 to 0 to 1] = { transpose(it, vector(faceSize, -4 to 2)) }
    this[-1 to 2 to 3] = { transpose(it, vector(faceSize, 4 to -2)) }

    this[-1 to 1 to 3] = { right(it, pivot10TL(faceSize, 3 to 1)).let { transpose(it, vector(faceSize, 0 to -4)) } }
    this[3 to -1 to 2] = { transpose(it, vector(faceSize, 0 to 4)).let { left(it, pivot10TL(faceSize, 3 to 1)) } }
}

fun vector(faceSize: Int, b: Block) = faceSize * b.first to faceSize * b.second

fun transpose(from: Pair<Point, Dir>, v: Point) = from.let { (p, d) ->
    p.first + v.first to p.second + v.second to d
}


private fun pivot10TL(faceSize: Int, b: Block): Point =
    (b.first * faceSize) * 10 + 5 to (b.second * faceSize) * 10 + 5

private fun pivot10BL(faceSize: Int, b: Block): Point =
    (b.first * faceSize + faceSize) * 10 + 5 to (b.second * faceSize) * 10 + 5

private fun pivot10TR(faceSize: Int, b: Block): Point =
    (b.first * faceSize) * 10 + 5 to (b.second * faceSize + faceSize) * 10 + 5

private fun pivot10BR(faceSize: Int, b: Block): Point =
    (b.first * faceSize + faceSize) * 10 + 5 to (b.second * faceSize + faceSize) * 10 + 5

private fun left(from: Pair<Point, Dir>, pivot10: Point): Pair<Point, Dir> {
    val (pos, dir) = from
    val (r0, c0) = pos
    val (rp, cp) = pivot10
    val dir1 = (dir + 3) % 4
    val r1 = rp - c0 * 10 + cp
    val c1 = cp + r0 * 10 - rp
    return r1 / 10 to c1 / 10 to dir1
}

private fun right(from: Pair<Point, Dir>, pivot10: Point): Pair<Point, Dir> {
    val (pos, dir) = from
    val (r0, c0) = pos
    val (rp, cp) = pivot10
    val dir1 = (dir + 1) % 4
    val r1 = rp + c0 * 10 - cp
    val c1 = cp - r0 * 10 + rp
    return r1 / 10 to c1 / 10 to dir1
}


private fun Board.stepPart2(from: Pair<Point, Dir>): Pair<Point, Dir> {
    val (pos, dir) = from
    val (r0, c0) = pos
    val rb0 = (r0 - 1) / faceSize
    val cb0 = (c0 - 1) / faceSize

    var pos1 = when (dir) {
        0 -> r0 to c0 + 1
        1 -> r0 + 1 to c0
        2 -> r0 to c0 - 1
        3 -> r0 - 1 to c0
        else -> wtf(dir)
    }
    var (r, c) = pos1

    val rb = if (r == 0) -1 else (r - 1) / faceSize
    val cb = if (c == 0) -1 else (c - 1) / faceSize
    return (if (rb == rb0 && cb == cb0) pos1 to dir
    else if (faceSize == 4) transitions4[rb to cb to dir]?.invoke(pos1 to dir) ?: TODO((rb to cb to dir).toString())
    else if (faceSize == 50) transitions50[rb to cb to dir]?.invoke(pos1 to dir) ?: TODO((rb to cb to dir).toString())
    else wtf(faceSize))
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
    val path = buildList {
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
//    execute(::part2, test, 5031)
    execute(::part2, input)
}

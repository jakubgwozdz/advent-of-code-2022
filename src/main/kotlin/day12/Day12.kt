package day12

import bfs
import execute
import readAllText

fun part1(input: String): Int = input.lineSequence().filterNot(String::isBlank)
    .toList()
    .let { lines ->
        val start = lines.indexOfFirst { it.contains("S") }.let { r -> r to lines[r].indexOf('S') }
        val end = lines.indexOfFirst { it.contains("E") }.let { r -> r to lines[r].indexOf('E') }
        bfs(graphOp = lines::possibleUp,
            start = start,
            initial = 0,
            moveOp = { l, _ -> l + 1 },
            endOp = { it == end }
        )!!
    }

fun part2(input: String): Int = input.lineSequence().filterNot(String::isBlank)
    .toList()
    .let { lines ->
        val end = lines.indexOfFirst { it.contains("E") }.let { r -> r to lines[r].indexOf('E') }
        bfs(graphOp = lines::possibleDown,
            start = end,
            initial = 0,
            moveOp = { l, _ -> l + 1 },
            endOp = { (r, c) -> lines[r][c] == 'a' }
        )!!
    }

fun List<String>.possibleUp(from: Pair<Int, Int>) = possibleMoves(from) { curr, next ->
    (next.isLowerCase() && curr.isLowerCase() && next <= curr) || next == curr + 1 ||
            (curr == 'S' && next == 'a') || (curr == 'z' && next == 'E')
}

fun List<String>.possibleDown(from: Pair<Int, Int>) = possibleMoves(from) { curr, next ->
    (next.isLowerCase() && curr.isLowerCase() && next >= curr) || next == curr - 1 ||
            (curr == 'E' && next == 'z')
}

fun List<String>.possibleMoves(from: Pair<Int, Int>, predicate: (Char, Char) -> Boolean) = buildList {
    val lines = this@possibleMoves
    val (r, c) = from
    val curr = lines[r][c]
    if (r - 1 in lines.indices && predicate(curr, lines[r - 1][c])) add(r - 1 to c)
    if (r + 1 in lines.indices && predicate(curr, lines[r + 1][c])) add(r + 1 to c)
    if (c - 1 in lines[r].indices && predicate(curr, lines[r][c - 1])) add(r to c - 1)
    if (c + 1 in lines[r].indices && predicate(curr, lines[r][c + 1])) add(r to c + 1)
}

fun main() {
    val input = readAllText("local/day12_input.txt")
    val test = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent()
    execute(::part1, test, 31)
    execute(::part1, input, 449)
    execute(::part2, test, 29)
    execute(::part2, input, 443)
}

package day23

import execute
import readAllText

fun part1(input: String) = parse(input)
    .let { elves -> (1..10).fold(elves, ::round) }
    .let { elves ->
        val rs = elves.minOf { it.first }..elves.maxOf { it.first }
        val cs = elves.minOf { it.second }..elves.maxOf { it.second }
        rs.sumOf { r -> cs.count { c -> (r to c) !in elves } }
    }

fun part2(input: String) = parse(input)
    .let { initial ->
        var round = 1
        var prev = initial
        while (true) {
            val next = round(prev, round)
            if (next == prev) break
            prev = next
            round++
        }
        round
    }

private fun parse(input: String) = input.lineSequence()
    .flatMapIndexed { r, l -> l.flatMapIndexed { c, char -> if (char == '#') listOf(r to c) else emptyList() } }
    .toSet()

fun Pos.adj() = sequence {
    (first - 1..first + 1).forEach { r ->
        (second - 1..second + 1).forEach { c ->
            if (r != first || c != second) yield(r to c)
        }
    }
}

typealias Pos = Pair<Int, Int>

operator fun Pos.plus(o: Pos) = first + o.first to second + o.second
operator fun Pos.plus(o: Dir2) = this + o.v

enum class Dir2(val v: Pos) {
    NW(-1 to -1), N(-1 to 0), NE(-1 to 1), W(0 to -1),
    E(0 to 1), SW(1 to -1), S(1 to 0), SE(1 to 1)
}

enum class Dir(val look: List<Dir2>, val move: Dir2) {
    N(listOf(Dir2.NW, Dir2.N, Dir2.NE), Dir2.N),
    S(listOf(Dir2.SW, Dir2.S, Dir2.SE), Dir2.S),
    W(listOf(Dir2.NW, Dir2.W, Dir2.SW), Dir2.W),
    E(listOf(Dir2.NE, Dir2.E, Dir2.SE), Dir2.E),
}

fun round(elves: Set<Pos>, round: Int): Set<Pos> {
//    println("== End of Round $round ==")
//    println("starting: $elves")
    val proposed = elves.associateWith { pos ->
        if (pos.adj().none { it in elves }) pos
        else (1..4).asSequence().map { Dir.values()[(it + round - 2) % 4] }.firstOrNull { dir ->
            dir.look.none { (pos + it) in elves }
        }?.let { dir -> pos + dir.move.v } ?: pos
    }
//    println("proposed: $proposed")
    val conflicts = proposed.toList().groupBy { it.second }
//    println("conflicts: $conflicts")
    val resolved = conflicts.flatMap { (proposed, wanting) ->
        if (wanting.size == 1) listOf(proposed)
        else wanting.map { it.first }
    }
//    println("resolved: $resolved")
    val result = resolved.toSet()
//    println("result: $result")


//    (-2..9).forEach { r->
//        (-3..10).forEach { c->
//            print(if (r to c in result) "#" else ".")
//        }
//        println()
//    }
//
//    println()

    check(result.size == elves.size)
    return result
}

fun main() {
    val input = readAllText("local/day23_input.txt")
    val test = """
        ....#..
        ..###.#
        #...#.#
        .#...##
        #.###..
        ##.#.##
        .#..#..
    """.trimIndent()
    execute(::part1, test, 110)
    execute(::part1, input)
    execute(::part2, test)
    execute(::part2, input)
}

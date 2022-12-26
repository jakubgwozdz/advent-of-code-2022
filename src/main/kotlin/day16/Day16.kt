package day16

import PriorityQueue
import bfs
import execute
import parseRecords
import readAllText

data class Valve(val rate: Long, val exits: List<String>)
class Graph(valves: Map<String, Valve>) {
    val data: List<Long>
    val moves: Map<Pair<Int, Int>, Move>
    val totalBits: Int

    init {
        var nextId = 1
        val idToNumbers = valves.filter { (k, v) -> v.rate > 0 || k == "AA" }
            .toList()
            .sortedByDescending { it.second.rate }
            .map { it.first }
            .associateWith { if (it == "AA") 0 else nextId++ }

        data = valves.filter { (k, v) -> v.rate > 0 || k == "AA" }
            .map { (k, v) -> idToNumbers[k]!! to v.rate }
            .sortedBy { it.first }
            .map { it.second }

        moves = valves
            .filter { (k, v) -> v.rate > 0 || k == "AA" }
            .keys.flatMap { s -> valves.filterValues { it.rate > 0 }.keys.map { e -> s to e } }
            .filter { (s, e) -> s != e }
            .associateWith { (s, e) ->
                bfs(
                    graphOp = { p -> valves[p]!!.exits },
                    start = s,
                    initial = listOf<String>(),
                    moveOp = { l, p -> l + p },
                    endOp = { it == e }
                )!!.let { Move(idToNumbers[e]!!, it.size) }
            }
            .mapKeys { (k, _) -> idToNumbers[k.first]!! to idToNumbers[k.second]!! }

        totalBits = (1..data.lastIndex).sumOf { 1 shl it }
    }

    operator fun get(pos: Int): Long = data[pos]
}

data class Move(
    val id: Int,
    val dist: Int,
)

data class State(
    val graph: Graph,
    val timeLeft: Int,
    val p1pos: Int = 0,
    val p2pos: Int = 0,
    val p1dist: Int = -1,
    val p2dist: Int = -1,
    val closed: Int = graph.totalBits,
    val score: Long = 0,
) {
    operator fun plus(actions: Pair<Move, Move>): State {
        val (a1, a2) = actions
        val newPos1 = a1.id
        val newPos2 = a2.id
        val newDist1 = a1.dist - 1
        val newDist2 = a2.dist - 1
        val newOpen = listOf(a1, a2).filter { it.dist == 0 }.map(Move::id)
        var timeElapsed = 1
        val newScore = score + newOpen.sumOf { graph[it] * (timeLeft - timeElapsed) }
        return copy(
            p1pos = newPos1,
            p2pos = newPos2,
            p1dist = newDist1,
            p2dist = newDist2,
            closed = closed - newOpen.sumOf { 1 shl it },
            timeLeft = timeLeft - timeElapsed,
            score = newScore,
        )
    }

    val potential: Long
        get() = (1..graph.data.lastIndex)
            .sumOf { if ((1 shl it) and this.closed > 0) graph.data[it] else 0 } * timeLeft

    override fun toString() =
        "@$p1pos($p1dist),$p2pos($p2dist), score $score, closed $closed, time left $timeLeft"

}

private fun State.possibleActions(): List<Pair<Move, Move>> = buildList {
    if (timeLeft > 0) {

        val ll1 = possibleForOne(p1pos, p1dist)
        val ll2 = possibleForOne(p2pos, p2dist)

        if (ll1.isNotEmpty() || ll2.isNotEmpty()) {
            ll1.ifEmpty { listOf(Move(p1pos, 1)) }.forEach { l1 ->
                ll2.ifEmpty { listOf(Move(p2pos, 1)) }.forEach { l2 ->
                    if (l1.id != l2.id) add(l1 to l2)
                }
            }

        }

    }
}

private fun State.possibleForOne(pos: Int, dist: Int) = buildList {
    if (dist > 0) add(Move(pos, dist))
    else if ((1 shl pos) and closed > 0) add(Move(pos, 0))
    else {
        val notCurr = (1..graph.data.lastIndex).filter { it != p1pos && it != p2pos && (1 shl it) and closed > 0 }
        val next = notCurr.associateWith { t -> graph.moves[pos to t] ?: error("No path from $pos to $t") }
        val reachable = next.filterValues { it.dist <= timeLeft }
        reachable.forEach { (v, d) -> add(d) }
    }
}

private fun search(graph: Graph, time: Int, players: Int): Long {
    val start = State(graph, time, p2dist = if (players == 1) time * 2 else -1)

    val comparator = compareByDescending<State> { it.score }

    val queue = PriorityQueue(comparator).apply { offer(start) }

    var result = 0L
    while (queue.isNotEmpty()) {
        val curr = queue.poll()
        if (result < curr.score) {
            result = curr.score
        }
        curr.possibleActions()
            .map { curr + it }
            .forEach { if (it.score + it.potential > result) queue.offer(it) }
    }
    return result
}

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .toMap()
    .let { data -> search(Graph(data), 30, 1) }

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .toMap()
    .let { data -> search(Graph(data), 26, 2) }

private val regex = "Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)".toRegex()
private fun parse(matchResult: MatchResult) =
    matchResult.destructured.let { (a, b, c) -> a to Valve(b.toLong(), c.split(", ")) }

fun main() {

    val input = readAllText("local/day16_input.txt")
    val test = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent()

    execute(::part1, test, 1651)
    execute(::part1, input, 2359)
    execute(::part2, test, 1707)
    execute(::part2, input)
}

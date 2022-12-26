package day16

import PriorityQueue
import bfs
import execute
import parseRecords
import readAllText
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

data class Valve(val rate: Long, val exits: List<String>)
class Graph(valves: Map<String, Valve>) {
    val data: List<Long>
    val moves: Map<Pair<Int, Int>, Move>

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
            .also { println(it.size) }
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

    }

    operator fun get(pos: Int): Long = data[pos]
}

sealed interface Action {
    val id: Int
    val dist: Int
}

data class Move(override val id: Int, override val dist: Int) : Action {
    override fun toString() = "->$id($dist)"
}

var pppp = 0L

data class State(
    val graph: Graph,
    val timeLeft: Int,
    val pos: List<Int>,
    val distances: List<Int> = pos.map { -1 },
    val open: Set<Int> = emptySet(),
    val score: Long = 0,
    val path: List<List<Action>> = emptyList(),
) {
    operator fun plus(actions: List<Action>): State {
        val newPos = actions.map { it.id }
        val newDist = actions.map { it.dist - 1 }
//        val newOpen = actions.filterIsInstance<Open>().map(Open::id)
        val newOpen = actions.filterIsInstance<Move>().filter { it.dist == 0 }.map(Move::id)
        var timeElapsed = 1
        val newScore = score + newOpen.sumOf { graph[it] * (timeLeft - timeElapsed) }
        return copy(
            pos = newPos,
            open = open + newOpen,
            distances = newDist,
            timeLeft = timeLeft - timeElapsed,
            score = newScore,
//            path = buildList { addAll(path); add(actions) }
        )
    }

    val closed by lazy { (1..graph.data.lastIndex) - open }

    override fun toString() =
        "@$pos($distances), score $score, closed $closed, time left $timeLeft, path $path"

}

private fun State.possibleActions(): List<List<Action>> = buildList {
    if (timeLeft > 0) {
        pos.mapIndexed { index, p ->
            buildList<Action> {
                if (distances[index] > 0) add(Move(p, distances[index]))
                else if (p in closed) add(Move(p, 0))
                else {
                    val notCurr = closed.filter { it != p && it !in pos }
                    val next = notCurr.associateWith { t -> graph.moves[p to t] ?: error("No path from $p to $t") }
                    val reachable = next.filterValues { it.dist <= timeLeft }
                    reachable.forEach { (v, d) -> add(d) }
                }
            }
        }.let { ll ->
            when (ll.size) {
                1 -> ll[0].forEach { l0 -> add(listOf(l0)) }
                2 -> {
                    ll[0].forEach { l0 ->
                        ll[1].forEach { l1 ->
                            if (l0.id != l1.id) add(listOf(l0, l1))
                        }
                    }
//                    if (ll[0].isEmpty() && ll[1].isNotEmpty()) ll[1].forEach { l1 -> add(listOf(Wait, l1)) }
//                    if (ll[0].isNotEmpty() && ll[1].isEmpty()) ll[0].forEach { l0 -> add(listOf(l0, Wait)) }
                    if (ll[0].isEmpty() && ll[1].isNotEmpty()) ll[1].forEach { l1 -> add(listOf(Move(pos[0], 1), l1)) }
                    if (ll[0].isNotEmpty() && ll[1].isEmpty()) ll[0].forEach { l0 -> add(listOf(l0, Move(pos[1], 1))) }
                }

                else -> TODO()
            }
        }
    }
}

private fun search(graph: Graph, time: Int, pos: List<Int>): Long {
    val start = State(graph, time, pos)

    val comparator = compareByDescending<State> { it.score }

    val queue = PriorityQueue<State>(comparator).apply { offer(start) }

    var result = 0L
    var rs = start
    var tested = 0L
    var mark = TimeSource.Monotonic.markNow()
    var prev = 0L
    var time = 0
    while (queue.isNotEmpty()) {
        val curr = queue.poll()
            .also {
//                if (pos.size > 1) {
//                    if (50 > pppp++) println(it) else TODO()
//                }
            }

        if (result < curr.score) {
            result = curr.score
            rs = curr
//            println(rs)
        }
        tested++
        if (mark.elapsedNow() > 1.seconds) {
            time++
//            println("${time}s: tested $tested, max $result, queue size ${queue.size}, rate = ${tested - prev}/s")
            mark = TimeSource.Monotonic.markNow()
            prev = tested
        }
        curr.possibleActions()
            .map { curr + it }
            .forEach { queue.offer(it) }
    }
//    }

//    println("tested: $tested")
//    println(rs)
    return result
}

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .toMap()
    .let { data -> search(Graph(data), 30, listOf(0)) }

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .toMap()
    .let { data -> search(Graph(data), 26, listOf(0, 0)) }

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

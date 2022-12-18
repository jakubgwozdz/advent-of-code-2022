package day16

import PriorityQueue
import bfs
import execute
import parseRecords
import readAllText
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

data class Valve(val rate: Long, val exits: List<String>)
class Graph(val valves: Map<String, Valve>) {
    operator fun get(pos: String): Valve = valves[pos]!!
    val shortest = valves
        .filter { (k, v) -> v.rate > 0 || k == "AA" }
        .keys.flatMap { s -> valves.filterValues { it.rate > 0 }.keys.map { e -> s to e } }
        .filter { (s, e) -> s != e }
        .associateWith { (s, e) ->
            bfs(
                graphOp = { p -> this[p].exits },
                start = s,
                initial = listOf<String>(),
                moveOp = { l, p -> l + p },
                endOp = { it == e }
            )!!.size
        }
//        .also {
//            it.forEach(::println)
////            TODO()
//        }
}

sealed interface Action

data class Move(val id: String, val dist: Int) : Action {
    override fun toString() = "->$id($dist)"
}

data class Open(val id: String) : Action {
    override fun toString() = "^$id"
}

object Wait : Action {
    override fun toString() = "<>"
}

var pppp = 0L

data class State(
    val graph: Graph,
    val timeLeft: Int,
    val pos: List<String>,
    val distances: List<Int> = pos.map { 0 },
    val open: Set<String> = emptySet(),
    val pressure: Long = 0,
    val soFar: Long = 0,
    val score: Long = 0,
    val path: List<List<Action>> = emptyList(),
) {
    operator fun plus(actions: List<Action>): State {
        val newPos = pos.zip(actions).map { (p, a) -> if (a is Move) a.id else p }
        val newDist = distances.zip(actions).map { (d, a) -> if (a is Move) a.dist - 1 else if (d > 0) d - 1 else 0 }
        val newOpen = actions.filterIsInstance<Open>().map(Open::id)
        val newScore = score + newOpen.sumOf { graph[it].rate * (timeLeft - 1) }
        return copy(
            pos = newPos,
            open = open + newOpen,
            distances = newDist,
            pressure = pressure + newOpen.sumOf { graph[it].rate },
            soFar = soFar + pressure,
            timeLeft = timeLeft - 1,
            score = newScore,
            path = buildList { addAll(path); add(actions) }
        )
    }

    val closed by lazy { graph.valves.filterValues { it.rate > 0 }.keys - open }
    fun excerpt() = pos.sorted().toString() to open.sorted().toString()
    fun fitness() = Fitness(soFar, pressure, timeLeft)

    override fun toString() =
        "@$pos($distances), score $score, closed $closed, pressure $pressure, so far $soFar, time left $timeLeft, path $path"

}

data class Fitness(val soFar: Long, val pressure: Long, val timeLeft: Int) {
    fun maybeBetterThan(prev: Fitness) = timeLeft > prev.timeLeft || soFar > prev.soFar
    fun surelyWorseThan(next: Fitness) = next.timeLeft >= timeLeft && next.soFar >= soFar
    override fun toString() = "($soFar,$timeLeft)"
}

private fun State.possibleActions(): List<List<Action>> = buildList {
    pos.mapIndexed { index, p ->
        buildList {
            if (timeLeft > 0) {
                if (distances[index] > 0) add(Wait)
                else if (index == 0 && p in closed) add(Open(p))
                else {
                    if (index != 0 && p in closed) add(Open(p))
                    val notCurr = closed.filter { it != p && it !in pos }
                    val next = notCurr.associateWith { t -> graph.shortest[p to t] ?: error("No path from $p to $t") }
                    val reachable = next.filterValues { it <= timeLeft }
                    reachable.forEach { (v, d) -> add(Move(v, d)) }
                    if (isEmpty()) {
                        add(Wait)
                    }
                }
            }
        }
    }.let { ll ->
        when (ll.size) {
            1 -> ll[0].forEach { l0 -> add(listOf(l0)) }
            2 -> ll[0].forEach { l0 ->
                ll[1].forEach { l1 ->
                    if ((l0 is Move && l1 is Open) || (l0 is Open && l1 is Move) ||
                        (l0 is Move && l1 is Move && l0.id != l1.id) ||
                        (l0 is Open && l1 is Open && l0.id != l1.id) ||
                        (l0 is Wait || l1 is Wait)
                    )
                        add(listOf(l0, l1))
                }
            }

            else -> TODO()
        }
    }
}

private fun search(graph: Graph, time: Int, pos: List<String>): Long {
    val start = State(graph, time, pos)

    val comparator = compareByDescending<State> { it.score }

    val queue = PriorityQueue<State>(comparator).apply { offer(start) }

    var result = 0L
    var rs = start
    var tested = 0L
    var mark = TimeSource.Monotonic.markNow()
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
        }
        tested++
//        if (curr.closed.isEmpty()) {
//            println(curr.path + " -> " + curr.soFar + " left " + curr.timeLeft)
//        }
        if (mark.elapsedNow() > 1.seconds) {
            println("tested $tested, max $result, queue size ${queue.size}")
            mark = TimeSource.Monotonic.markNow()
        }
        curr.possibleActions()
            .map { curr + it }
//                .filter { state ->
//                    state.excerpt() !in visited || visited[state.excerpt()]!!.any {
//                        state.maybeBetterThan(it)
//                    }
//                }
            .forEach { queue.offer(it) }
    }
//    }

    println(rs)
    return result
}

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .toMap()
    .let { data -> search(Graph(data), 30, listOf("AA")) }

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .toMap()
    .let { data -> search(Graph(data), 26, listOf("AA", "AA")) }

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
//    TODO()
    execute(::part2, test, 1707)
    execute(::part2, input)
}

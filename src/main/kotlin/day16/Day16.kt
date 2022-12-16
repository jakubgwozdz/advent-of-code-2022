package day16

import Stack
import bfs
import execute
import parseRecords
import readAllText
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

data class Valve(val rate: Long, val exits: List<String>)
class Graph(val valves: Map<String, Valve>) {
    operator fun get(pos: String): Valve = valves[pos]!!
    val shortest = valves.keys.flatMap { s -> valves.keys.map { e -> s to e } }
        .filter { (s, e) -> s != e }
        .associateWith { (s, e) ->
            bfs(
                graphOp = { p -> this[p].exits },
                start = s,
                initial = listOf<String>(),
                moveOp = { l, p -> l + p },
                endOp = { it == e }
            )!!.run { first() to size }
        }
//        .onEach { println(it) }
//        .also { TODO() }
}

sealed interface Action

data class Move(val id: String, val target: String) : Action {
    override fun toString() = "->$id($target)"
}

data class Open(val id: String) : Action {
    override fun toString() = "^$id"
}

object Wait : Action {
    override fun toString() = "<>"
}

data class State(
    val graph: Graph,
    val timeLeft: Int,
    val pos: List<String>,
    val targets: List<String?> = pos.map { null },
    val open: Set<String> = emptySet(),
    val pressure: Long = 0,
    val soFar: Long = 0,
    val path: List<List<Action>> = emptyList(),
) {
    operator fun plus(actions: List<Action>): State {
        val newPos = pos.zip(actions).map { (p, a) -> if (a is Move) a.id else p }
        val newTargets = newPos.zip(actions).map { (p, a) -> if (a is Move && a.target != p) a.target else null }
        val newOpen = actions.filterIsInstance<Open>().map(Open::id)
        return copy(
            pos = newPos,
            open = open + newOpen,
            targets = newTargets,
            pressure = pressure + newOpen.sumOf { graph[it].rate },
            soFar = soFar + pressure,
            timeLeft = timeLeft - 1,
            path = buildList { addAll(path); add(actions) }
        )
//            .also {
//                if (shouldPrint(it)) {
//                    println("$this + $actions -> $it")
//                }
//            }
    }

    val closed by lazy { graph.valves.filterValues { it.rate > 0 }.keys - open }
    fun excerpt() = pos.sorted().toString() to open.sorted().toString()
    fun fitness() = Fitness(soFar, pressure, timeLeft)

    override fun toString() = "@$pos->$targets, open $open, pressure $pressure, so far $soFar, time left $timeLeft"
    fun surelyWorseThan(next: State) = fitness().surelyWorseThan(next.fitness())
    fun maybeBetterThan(prev: State) = fitness().maybeBetterThan(prev.fitness())
}

data class Fitness(val soFar: Long, val pressure: Long, val timeLeft: Int) {
    fun maybeBetterThan(prev: Fitness) = timeLeft > prev.timeLeft || soFar > prev.soFar
    fun surelyWorseThan(next: Fitness) = next.timeLeft >= timeLeft && next.soFar >= soFar
    override fun toString() = "($soFar,$timeLeft)"
}

private fun State.possibleActions(): List<List<Action>> = buildList {
    pos.mapIndexed { index, p ->
        buildList {
            if (index == 0 && p in closed && targets[index] == null && timeLeft > 0) add(Open(p))
            else if (timeLeft > 0) {
                if (index != 0 && p in closed && targets[index] == null) add(Open(p))
                val notCurr = closed.filter { it != p }
                val notStray = notCurr.filter { targets[index] == null || targets[index] == it }
                val next = notStray.map { t -> (graph.shortest[p to t] ?: error("No path from $p to $t")) to t }
                val reachable = next.filter { it.first.second <= timeLeft }
                val unique = reachable.map { it.first.first to it.second }.toSet()
                unique.forEach { add(Move(it.first, it.second)) }
                if (isEmpty()) {
                    add(Wait)
                }
            }
        }
    }.let { ll ->
        when (ll.size) {
            1 -> ll[0].forEach { l0 -> add(listOf(l0)) }
            2 -> ll[0].forEach { l0 ->
                ll[1].forEach { l1 ->
                    if (pos[0] != pos[1] || (l0 is Move && l1 is Open) || (l0 is Open && l1 is Move) ||
                        (l0 is Move && l1 is Move && l0.id <= l1.id && l0.target != l1.target) ||
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

    val comparator = compareByDescending<State> { it.soFar }

    val queue = Stack<State>().apply { offer(start) }

    var result = 0L
    var rs = start
    var tested = 0L
    var mark = TimeSource.Monotonic.markNow()
    while (queue.isNotEmpty()) {
        val curr = queue.poll()
        if (result < curr.soFar) {
            result = curr.soFar
            rs = curr
        }
        tested++
        if (curr.closed.isEmpty()) {
            println(curr.path + " -> " + curr.soFar + " left " + curr.timeLeft)
        }
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

private fun merge(
    prevStates: List<State>?,
    curr: State
) = (((prevStates ?: setOf())).filter { !it.surelyWorseThan(curr) } + curr)
    .also {
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

//    execute(::part1, test, 1651)
//    execute(::part1, input, 2359)
    execute(::part2, test, 1707)
    execute(::part2, input)
}

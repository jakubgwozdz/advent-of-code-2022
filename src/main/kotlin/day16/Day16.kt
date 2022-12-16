package day16

import Stack
import execute
import parseRecords
import readAllText
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

data class Valve(val rate: Long, val exits: List<String>)
data class Graph(val valves: Map<String, Valve>) {
    operator fun get(pos: String): Valve = valves[pos]!!
}

sealed interface Action
data class Move(val id: String) : Action
data class Open(val id: String) : Action
object Wait : Action

data class State(
    val pos: List<String>,
    val open: Set<String>,
    val pressure: Long,
    val soFar: Long,
    val timeLeft: Int,
    val graph: Graph,
) {
    operator fun plus(actions: List<Action>): State {
        val newPos = pos.zip(actions).map { (p, a) -> if (a is Move) a.id else p }
        val newOpen = actions.filterIsInstance<Open>().map(Open::id)
        return copy(
            pos = newPos,
            open = open + newOpen,
            pressure = pressure + newOpen.sumOf { graph[it].rate },
            soFar = soFar + pressure,
            timeLeft = timeLeft - 1
        )
    }

    fun excerpt() = pos.sorted().toString() to open.sorted().toString()
    fun fitness() = Fitness(soFar, pressure, timeLeft)

    override fun toString() = "@$pos, open $open, pressure $pressure, so far $soFar, time left $timeLeft"
    fun surelyWorseThan(next: State) = fitness().surelyWorseThan(next.fitness())
    fun maybeBetterThan(prev: State) = fitness().maybeBetterThan(prev.fitness())
}

data class Fitness(val soFar: Long, val pressure: Long, val timeLeft: Int) {
    fun maybeBetterThan(prev: Fitness) = timeLeft > prev.timeLeft || soFar > prev.soFar
    fun surelyWorseThan(next: Fitness) = next.timeLeft >= timeLeft && next.soFar >= soFar
    override fun toString() = "($soFar,$timeLeft)"
}


var testing = false
private fun State.possibleActions(): List<List<Action>> = buildList {
//        if (timeLeft > 0) add(
//            when (31 - timeLeft) {
//            1 -> Move("DD")
//            2 -> Open("DD")
//            3 -> Move("CC")
//            4 -> Move("BB")
//            5 -> Open("BB")
//            6 -> Move("AA")
//            7 -> Move("II")
//            8 -> Move("JJ")
//            9 -> Open("JJ")
//            10 -> Move("II")
//            11 -> Move("AA")
//            12 -> Move("DD")
//            13 -> Move("EE")
//            14 -> Move("FF")
//            15 -> Move("GG")
//            16 -> Move("HH")
//            17 -> Open("HH")
//            18 -> Move("GG")
//            19 -> Move("FF")
//            20 -> Move("EE")
//            21 -> Open("EE")
//            22 -> Move("DD")
//            23 -> Move("CC")
//            24 -> Open("CC")
//            else -> Wait
//    } else
    if (timeLeft > 0) {
        pos.map { p ->
            val (rate, exits) = graph[p]
            buildList {
                exits.forEach { exit -> add(Move(exit)) }
                if (rate > 0 && p !in open) add(Open(p))
            }
        }.let { ll ->
            when (ll.size) {
                1 -> ll[0].forEach { l0 -> add(listOf(l0)) }
                2 -> ll[0].forEach { l0 ->
                    ll[1].forEach { l1 ->
                        if (pos[0] != pos[1] || (l0 is Move && l1 is Open) || (l0 is Open && l1 is Move) ||
                            (l0 is Move && l1 is Move && l0.id <= l1.id) ||
                            (l0 is Open && l1 is Open && l0.id != l1.id)
                        )
                            add(listOf(l0, l1))
                    }
                }

                else -> TODO()
            }
        }
    }
}

private fun search(graph: Graph, time: Int, pos: List<String>): Long {
    val start = State(pos, emptySet(), 0, 0, time, graph)

    val comparator = compareByDescending<State> { it.soFar }

    val queue = Stack<State>().apply { offer(start) }

    val visited = mutableMapOf(start.excerpt() to listOf(start)).apply { clear() }

    var result = 0L
    var tested = 0L
    var mark = TimeSource.Monotonic.markNow()
    while (queue.isNotEmpty()) {
        val curr = queue.poll()
        val excerpt = curr.excerpt()
        val prevFitness = visited[excerpt] ?: emptyList()
        if (prevFitness.any { curr.surelyWorseThan(it) })
            continue
        val newFitness = merge(prevFitness, curr)
        result = result.coerceAtLeast(curr.soFar)
        val goDeep = prevFitness.isEmpty() || prevFitness.any { curr.maybeBetterThan(it) }
        if (goDeep) {
            visited[excerpt] = newFitness
            result = result.coerceAtLeast(curr.soFar)
            tested++
            if (mark.elapsedNow() > 1.seconds) {
                println("tested $tested, max $result, queue size ${queue.size}, curr $curr")
                mark = TimeSource.Monotonic.markNow()
            }
            curr.possibleActions()
                .map { curr + it }
                .filter { state ->
                    state.excerpt() !in visited || visited[state.excerpt()]!!.any {
                        state.maybeBetterThan(it)
                    }
                }
                .forEach { queue.offer(it) }
        }
    }

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

    execute(::part1, test, 1651)
//    execute(::part1, input, 2359)
    execute(::part2, test, 1707)
    execute(::part2, input)
}

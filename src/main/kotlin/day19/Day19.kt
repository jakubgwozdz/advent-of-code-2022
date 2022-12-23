package day19

import PriorityQueue
import Stack
import execute
import parseRecords
import readAllText
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .map { it.id to it.score(24) }
    .sumOf { (id, score) -> id * score }

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .take(3)
    .map { it.score(32) }
    .fold(1L, Long::times)

private data class Blueprint(
    val id: Int,
    val oreRobotOres: Long,
    val clayRobotOres: Long,
    val obsidianRobotOres: Long,
    val obsidianRobotClays: Long,
    val geodeRobotOres: Long,
    val geodeRobotObsidians: Long,
) {
    val maxOreRequired by lazy { listOf(oreRobotOres, clayRobotOres, obsidianRobotOres, geodeRobotOres).max() }
}

private data class State(
    val timeLeft: Int,
    val geodeRobots: Long = 0, val geodes: Long = 0, val obsidianRobots: Long = 0, val obsidians: Long = 0,
    val clayRobots: Long = 0, val clays: Long = 0, val oreRobots: Long = 1, val ores: Long = 0,
) : Comparable<State> {
    override fun compareTo(other: State): Int = comparator.compare(this, other)
}

private fun State.score() = geodes + geodeRobots * timeLeft

private enum class Order { None, OreRobot, ClayRobot, ObsidianRobot, GeodeRobot }

private val comparator = compareByDescending<State> { it.score() }
    .thenByDescending { it.obsidians + it.obsidianRobots * it.timeLeft }
    .thenByDescending { it.clays + it.clayRobots * it.timeLeft }
    .thenByDescending { it.ores + it.oreRobots * it.timeLeft }

private fun Blueprint.exits(state: State) =
    if (!canBuildMoreGeodeRobots(state)) emptyList() else possibleOrders(state).map {
        var ores1 = state.ores + state.oreRobots
        var clays1 = state.clays + state.clayRobots
        var obsidians1 = state.obsidians + state.obsidianRobots
        var geode1 = state.geodes + state.geodeRobots
        var oreRobots1 = state.oreRobots
        var clayRobots1 = state.clayRobots
        var obsidianRobots1 = state.obsidianRobots
        var geodeRobots1 = state.geodeRobots
        when (it) {
            Order.OreRobot -> {
                ores1 -= oreRobotOres
                oreRobots1++
            }

            Order.ClayRobot -> {
                ores1 -= clayRobotOres
                clayRobots1++
            }

            Order.ObsidianRobot -> {
                ores1 -= obsidianRobotOres
                clays1 -= obsidianRobotClays
                obsidianRobots1++
            }

            Order.GeodeRobot -> {
                ores1 -= geodeRobotOres
                obsidians1 -= geodeRobotObsidians
                geodeRobots1++
            }

            Order.None -> {
            }
        }
        check(ores1 >= 0)
        check(clays1 >= 0)
        check(obsidians1 >= 0)
        check(geode1 >= 0)
        State(
            timeLeft = state.timeLeft - 1,
            geodeRobots = geodeRobots1, geodes = geode1, obsidianRobots = obsidianRobots1, obsidians = obsidians1,
            clayRobots = clayRobots1, clays = clays1, oreRobots = oreRobots1, ores = ores1,
        )
    }.sortedWith(comparator)

private fun Blueprint.canBuildMoreGeodeRobots(state: State): Boolean {
    val maxPossibleOres = state.ores + state.oreRobots * state.timeLeft + state.timeLeft * state.timeLeft / 2
    val maxPossibleObsidians =
        state.obsidians + state.obsidianRobots * state.timeLeft + state.timeLeft * state.timeLeft / 2
    return state.timeLeft > 0 && geodeRobotOres <= maxPossibleOres && geodeRobotObsidians <= maxPossibleObsidians
}

private fun Blueprint.possibleOrders(state: State) = buildList {
    val canBuildGeodeRobot = state.ores >= geodeRobotOres && state.obsidians >= geodeRobotObsidians
    val canBuildObsidianRobot = state.ores >= obsidianRobotOres && state.clays >= obsidianRobotClays &&
            state.obsidianRobots < geodeRobotObsidians
    val canBuildClayRobot = state.ores >= clayRobotOres && state.clayRobots < obsidianRobotClays
    val canBuildOreRobot = state.ores >= oreRobotOres && state.oreRobots < maxOreRequired

    if (canBuildGeodeRobot) add(Order.GeodeRobot)
    else {
        if (canBuildObsidianRobot) add(Order.ObsidianRobot)
        if (canBuildClayRobot) add(Order.ClayRobot)
        if (canBuildOreRobot) add(Order.OreRobot)
        add(Order.None)
    }
}


private typealias StateWithTimeLeft = Pair<State, Int>

private fun StateWithTimeLeft.stronglyBetter(that: StateWithTimeLeft) =
    this != that && this.second >= that.second
            && this.first.geodeRobots >= that.first.geodeRobots
            && this.first.geodes >= that.first.geodes
            && this.first.obsidianRobots >= that.first.obsidianRobots
            && this.first.obsidians >= that.first.obsidians
            && this.first.clayRobots >= that.first.clayRobots
            && this.first.clays >= that.first.clays
            && this.first.oreRobots >= that.first.oreRobots
            && this.first.ores >= that.first.ores

//class FilteringQueue<E : Any>(val comparator: Comparator<E>, val betterOp: (E, E) -> Boolean) : Queue<E>() {
//    override fun offer(e: E) {
//        val worth = backing.isEmpty() || !backing.all { betterOp(it, e) }
//        backing.removeIf { betterOp(e, it) }
//
//        if (worth) {
//            val index = backing.binarySearch(e, comparator).let {
//                if (it < 0) -it - 1 else it
//            }
//            backing.add(index, e)
//        }
//    }
//}
//
class FilteringStack<E : Any>(val betterOp: (E, E) -> Boolean) : Stack<E>() {
    override fun offer(e: E) {
        val worth = backing.isEmpty() || !backing.all { betterOp(it, e) }
        backing.removeIf { betterOp(e, it) }

        if (worth) {
            backing.add(e)
        }
    }
}

private fun Blueprint.score(time: Int): Long {
    var tested = 0L
    var best = State(time)
    val stack =
//        FilteringStack<StateWithTimeLeft>(
////        comparator = { (s1, t1), (s2, t2) -> s1.score(t1).compareTo(s2.score(t2)) },
//            betterOp = { s1, s2 -> s1.stronglyBetter(s2) }
//        )
        PriorityQueue<State>(comparator = comparator)
            .apply { offer(best) }
    val states = mutableSetOf<State>()
    var mark = TimeSource.Monotonic.markNow()
    while (stack.isNotEmpty()) {
        tested++
        val state = stack.poll()
        if (state !in states) {
            states += state
//            states[state] = timeLeft
            if (state.score() > best.score()) {
                best = state
                    .also { println("state $state with score ${state.score()}") }
            }
            this.exits(state)
//                    .also {
//                        if (tested < 100) println("$timeLeft: $state -> $it")
//                    }
                .forEach(stack::offer)
            if (mark.elapsedNow() > 1.seconds) {
                println("tested $tested, stack size ${stack.size}, states ${states.size}")
                mark = TimeSource.Monotonic.markNow()
            }
        }
    }
    println(tested)
    return best.score().also { println("$best gives $it geodes") }
}

private val regex =
    "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()

private fun parse(matchResult: MatchResult) = matchResult.destructured.let { (a, b, c, d, e, f, g) ->
    Blueprint(a.toInt(), b.toLong(), c.toLong(), d.toLong(), e.toLong(), f.toLong(), g.toLong())
}

fun main() {
    val input = readAllText("local/day19_input.txt")
    val test = """
        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
            """.trimIndent()
    execute(::part1, test, 33)
    execute(::part1, input, 817)
    execute(::part2, test, 56 * 62)
    execute(::part2, input)
}

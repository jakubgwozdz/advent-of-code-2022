package day19

import Stack
import execute
import parseRecords
import readAllText
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

fun part1(input: String) = input.parseRecords(regex, ::parse)
    .sumOf(Blueprint::quality)

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
)

private data class State(
    val geodeRobots: Long = 0, val geodes: Long = 0, val obsidianRobots: Long = 0, val obsidians: Long = 0,
    val clayRobots: Long = 0, val clays: Long = 0, val oreRobots: Long = 1, val ores: Long = 0,
)

private fun State.score(timeLeft: Int) = geodeRobots * timeLeft + geodes

private enum class Order { None, OreRobot, ClayRobot, ObsidianRobot, GeodeRobot }

private fun comparator(timeLeft: Int) = compareBy<State> { it.score(timeLeft) }
    .thenBy { it.obsidians + it.obsidianRobots * timeLeft }
    .thenBy { it.clays + it.clayRobots * timeLeft }
    .thenBy { it.ores + it.oreRobots * timeLeft }

private fun Blueprint.exits(state: State, timeLeft: Int) = buildList {
    if (timeLeft <= 0) {
//    } else if (state.obsidianRobots == 0L && timeLeft <= geodeRobotObsidians) {
    } else
        if (state.ores >= geodeRobotOres && state.obsidians >= geodeRobotObsidians) this.add(Order.GeodeRobot)
        else if (state.ores >= obsidianRobotOres && state.obsidians >= obsidianRobotClays &&
            state.obsidianRobots < geodeRobotObsidians &&
            state.oreRobots >= geodeRobotOres + obsidianRobotOres - 1 &&
            state.oreRobots >= clayRobotOres + obsidianRobotOres - 1 &&
            state.oreRobots >= oreRobotOres + obsidianRobotOres - 1
        ) this.add(Order.ObsidianRobot)
        else if (state.ores >= clayRobotOres && state.obsidians >= obsidianRobotClays &&
            state.obsidianRobots < geodeRobotObsidians &&
            state.oreRobots >= geodeRobotOres + clayRobotOres - 1 &&
            state.oreRobots >= obsidianRobotOres + clayRobotOres - 1 &&
            state.oreRobots >= oreRobotOres + clayRobotOres - 1
        ) this.add(Order.ClayRobot)
        else {
            if (state.ores >= obsidianRobotOres && state.clays >= obsidianRobotClays && state.obsidianRobots < geodeRobotObsidians) this.add(
                Order.ObsidianRobot
            )
            if (state.ores >= clayRobotOres && state.clayRobots < obsidianRobotClays) this.add(Order.ClayRobot)
            if (state.ores >= oreRobotOres && state.oreRobots < obsidianRobotOres + clayRobotOres + geodeRobotOres) this.add(
                Order.OreRobot
            )
            if (state.ores < oreRobotOres ||
                state.ores < clayRobotOres ||
                state.ores < obsidianRobotOres ||
                state.ores < geodeRobotOres ||
                state.clayRobots > 0 && state.clays < obsidianRobotClays ||
                state.obsidianRobots > 0 && state.obsidians < geodeRobotObsidians
            )
                this.add(Order.None)
        }
}.map {
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
    State(
        geodeRobots = geodeRobots1, geodes = geode1, obsidianRobots = obsidianRobots1, obsidians = obsidians1,
        clayRobots = clayRobots1, clays = clays1, oreRobots = oreRobots1, ores = ores1,
    )
}.sortedWith(comparator(timeLeft))

private fun Blueprint.quality(): Long {
    val time = 24

    val score = score(time)
    return id * (score)
}

private fun Blueprint.score(time: Int): Long {
    var tested = 0L
    var best: State = State()
    var bestTL = time
    val stack = Stack<Pair<State, Int>>()
        .apply { offer(best to time) }
    val states = mutableMapOf<State, Int>()
    var mark = TimeSource.Monotonic.markNow()
    while (stack.isNotEmpty()) {
        tested++
        val (state, timeLeft) = stack.poll()
        if (states[state].let { it == null || it < timeLeft }) {

            states[state] = timeLeft
            if (state.score(timeLeft) > best.score(bestTL)) {
                best = state
                    .also { println("state $state @ $timeLeft = ${state.score(timeLeft)}") }
                bestTL = timeLeft
            }
            if (timeLeft > 0) {
                this.exits(state, timeLeft)
//                    .also {
//                        if (tested < 100) println("$timeLeft: $state -> $it")
//                    }
                    .forEach {
                        stack.offer(it to timeLeft - 1)
                    }
            }
        }
        if (mark.elapsedNow() > 1.seconds) {
            println("tested $tested, stack size ${stack.size}, states ${states.size}")
            mark = TimeSource.Monotonic.markNow()
        }
    }
    println(tested)
    return best.score(bestTL)
        .also { println("$best@$bestTL gives $it geodes") }
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
//    execute(::part2, test, 56 * 62)
//    execute(::part2, input)
}

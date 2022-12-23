package day19

import Stack
import execute
import parseRecords
import readAllText

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
    val maxOreRequired = maxOf(oreRobotOres, clayRobotOres, obsidianRobotOres, geodeRobotOres)
}

private data class State(
    val timeLeft: Int,
    val geodeRobots: Long = 0, val geodes: Long = 0, val obsidianRobots: Long = 0, val obsidians: Long = 0,
    val clayRobots: Long = 0, val clays: Long = 0, val oreRobots: Long = 1, val ores: Long = 0,
)

private fun State.score() = geodes + geodeRobots * timeLeft

private enum class Order { OreRobot, ClayRobot, ObsidianRobot, GeodeRobot }

private fun Blueprint.exits(state: State) =
    if (!canBuildMoreGeodeRobots(state)) emptyList() else possibleOrders(state).map { (order, time) ->
        var ores = state.ores + state.oreRobots * time
        var clays = state.clays + state.clayRobots * time
        var obsidians = state.obsidians + state.obsidianRobots * time
        var geode = state.geodes + state.geodeRobots * time
        var oreRobots = state.oreRobots
        var clayRobots = state.clayRobots
        var obsidianRobots = state.obsidianRobots
        var geodeRobots = state.geodeRobots
        val timeLeft = state.timeLeft - time
        when (order) {
            Order.OreRobot -> {
                ores -= oreRobotOres
                oreRobots++
            }

            Order.ClayRobot -> {
                ores -= clayRobotOres
                clayRobots++
            }

            Order.ObsidianRobot -> {
                ores -= obsidianRobotOres
                clays -= obsidianRobotClays
                obsidianRobots++
            }

            Order.GeodeRobot -> {
                ores -= geodeRobotOres
                obsidians -= geodeRobotObsidians
                geodeRobots++
            }
        }
        check(ores >= 0)
        check(clays >= 0)
        check(obsidians >= 0)
        check(geode >= 0)
        check(timeLeft >= 0)
        State(
            timeLeft, geodeRobots, geode, obsidianRobots, obsidians, clayRobots, clays, oreRobots, ores,
        )
    }

private fun Blueprint.canBuildMoreGeodeRobots(state: State): Boolean {
    val maxPossibleOres = state.ores + state.oreRobots * state.timeLeft + state.timeLeft * state.timeLeft / 2
    val maxPossibleObsidians =
        state.obsidians + state.obsidianRobots * state.timeLeft + state.timeLeft * state.timeLeft / 2
    return state.timeLeft > 0 && geodeRobotOres <= maxPossibleOres && geodeRobotObsidians <= maxPossibleObsidians
}

private fun Blueprint.possibleOrders(state: State) = buildList {
    val timeToBuildGeodeRobot = if (state.obsidianRobots == 0L) state.timeLeft + 1
    else if (geodeRobotOres <= state.ores && geodeRobotObsidians <= state.obsidians) 1
    else maxOf(
        1L,
        (geodeRobotOres - state.ores - 1) / state.oreRobots + 2L,
        (geodeRobotObsidians - state.obsidians - 1) / state.obsidianRobots + 2L
    ).toInt()
    val timeToBuildObsidianRobot = if (state.clayRobots == 0L) state.timeLeft + 1
    else if (obsidianRobotOres <= state.ores && obsidianRobotClays <= state.clays) 1
    else maxOf(
        1L,
        (obsidianRobotOres - state.ores - 1) / state.oreRobots + 2L,
        (obsidianRobotClays - state.clays - 1) / state.clayRobots + 2L
    ).toInt()
    val timeToBuildClayRobot = if (clayRobotOres <= state.ores) 1
    else if (clayRobotOres <= state.ores) 1
    else maxOf(
        1L,
        (clayRobotOres - state.ores - 1) / state.oreRobots + 2L,
    ).toInt()
    val timeToBuildOreRobot = if (oreRobotOres <= state.ores) 1
    else if (oreRobotOres <= state.ores) 1
    else maxOf(
        1L,
        (oreRobotOres - state.ores - 1) / state.oreRobots + 2L,
    ).toInt()
    if (timeToBuildGeodeRobot == 1) add(Order.GeodeRobot to timeToBuildGeodeRobot)
    else {
        if (timeToBuildGeodeRobot < state.timeLeft)
            add(Order.GeodeRobot to timeToBuildGeodeRobot)
        if (timeToBuildObsidianRobot < state.timeLeft && state.obsidianRobots < geodeRobotObsidians)
            add(Order.ObsidianRobot to timeToBuildObsidianRobot)
        if (timeToBuildClayRobot < state.timeLeft && state.clayRobots < obsidianRobotClays)
            add(Order.ClayRobot to timeToBuildClayRobot)
        if (timeToBuildOreRobot < state.timeLeft && state.oreRobots < maxOreRequired)
            add(Order.OreRobot to timeToBuildOreRobot)
    }
}


private fun Blueprint.score(time: Int): Long {
    var best = State(time)
    val stack = Stack<State>()
        .apply { offer(best) }
    while (stack.isNotEmpty()) {
        val state = stack.poll()
        if (state.score() > best.score()) best = state
        this.exits(state).forEach(stack::offer)
    }
    return best.score()
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
    execute(::part2, input, 4216)
}

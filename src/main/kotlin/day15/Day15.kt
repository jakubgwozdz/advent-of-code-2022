package day15

import execute
import parseRecords
import readAllText
import kotlin.math.absoluteValue

tailrec fun LongRange.splitRange(
    cuts: List<Long>,
    cutIndex: Int = 0,
    acc: List<LongRange> = emptyList()
): List<LongRange> =
    if (first > last) acc
    else if (cuts.lastIndex < cutIndex) acc + listOf(this)
    else {
        val c = cuts[cutIndex]
        if (c < first) splitRange(cuts, cutIndex + 1, acc)
        else (c + 1..last).splitRange(
            cuts,
            cutIndex + 1,
            acc + listOf(first until c, c..c).filter { it.first <= it.last }
        )
    }

fun <T> List<T>.merge(predicate: (T, T) -> Boolean, mergeOp: (T, T) -> T): List<T> =
    fold<T, MutableList<T>>(mutableListOf()) { acc, t ->
        acc.apply {
            if (isEmpty()) add(t)
            else if (predicate(last(), t)) add(mergeOp(removeLast(), t))
            else add(t)
        }
    }.toList()

fun part1(input: String): Long {
    val data = input.parseRecords(regex, ::parse).toList()
    val testLine = if (data.first().first.first == 2L) 10L else 2000000L
    val beaconsAlreadyOnTestLine = data
        .map { (_, beacon) -> beacon }
        .filter { (_, y) -> y == testLine }
        .toSet().size
    val ranges = data
        .map { (sensor, beacon) ->
            val (sx, sy) = sensor
            val (bx, by) = beacon
            val dist = (sx - bx).absoluteValue + (sy - by).absoluteValue
            val dist10 = dist - (sy - testLine).absoluteValue
            sx - dist10..sx + dist10
        }
        .filter { it.first <= it.last }
        .sortedWith(compareBy<LongRange> { it.first }.thenBy { it.last })

    val cuts = buildSet { ranges.forEach { add(it.first); add(it.last) } }
        .toList().sorted()

    return ranges.flatMap { it.splitRange(cuts) }
        .map { it.first to it.last }
        .filter { (s, e) -> s <= e }
        .toSet().sumOf { (s, e) -> e - s + 1 } - beaconsAlreadyOnTestLine
}

fun part2(input: String): Long {
    val data = input.parseRecords(regex, ::parse).toList()
    val sensors = data.map { (sensor, beacon) ->
        val (sx, sy) = sensor
        val (bx, by) = beacon
        val dist = (sx - bx).absoluteValue + (sy - by).absoluteValue
        sensor to dist
    }

    val sensors45 = sensors.map { (s, r) ->
        val (x0, y0) = s
        val x1 = x0 - y0
        val y1 = x0 + y0
        x1 to y1 to r
    }

    val ranges45 = sensors45.map { (s, d) ->
        val (x, y) = s
        (x - d..x + d) to (y - d..y + d)
    }

    val horizCuts45 = buildSet { ranges45.forEach { (h, v) -> add(h.first); add(h.last) } }
        .toList().sorted()

    val vertCuts45 = buildSet { ranges45.forEach { (h, v) -> add(v.first); add(v.last) } }
        .toList().sorted()

    val blocks45: List<Pair<LongRange, List<LongRange>>> = ranges45.flatMap { (h, v) ->
        h.splitRange(horizCuts45).flatMap { hh -> v.splitRange(vertCuts45).map { vv -> hh to vv } }
    }
        .asSequence()
        .sortedWith(compareBy<Pair<LongRange, LongRange>> { it.first.first }.thenBy { it.first.last }
            .thenBy { it.second.first }.thenBy { it.second.last })
        .groupBy { it.first }.map { (h, l) -> h to l.map { it.second } }
        .map { (h, vl) ->
            h to vl.merge(
                predicate = { a, b -> a.last >= b.first - 1 },
                mergeOp = { a, b -> a.first..b.last }
            )
        }
        .merge(
            predicate = { (a, al), (b, bl) -> a.last >= b.first - 1 && al == bl },
            mergeOp = { (a, al), (b, bl) -> a.first..b.last to al }
        )

    val invalid45 = blocks45.first { it.second.size > 1 }
        .let { (h, vl) -> h.single() to vl.first().last + 1 }

    val invalid = invalid45.let { (x1, y1) ->
        val x0 = (x1 + y1) / 2
        val y0 = x0 - x1
        x0 to y0
    }

    return invalid.let { (x, y) -> y + x * 4000000 }
}

private val regex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
private fun parse(matchResult: MatchResult) = matchResult.destructured.let { (a, b, c, d) ->
    (a.toLong() to b.toLong()) to (c.toLong() to d.toLong())
}

fun main() {
    val input = readAllText("local/day15_input.txt")
    val test = """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent()
    execute(::part1, test, 26)
    execute(::part1, input, 5525847)
    execute(::part2, test, 56000011)
    execute(::part2, input, 13340867187704)
}

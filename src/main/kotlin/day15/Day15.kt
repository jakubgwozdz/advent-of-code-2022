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
            sx - dist10 to sx + dist10
        }
        .filter { (s, e) -> s <= e }
        .sortedWith(compareBy<Pair<Long, Long>> { it.first }.thenBy { it.second })

    val cuts = buildSet { ranges.forEach { (s, e) -> add(s); add(e) } }
        .toList().sorted()

    return ranges.flatMap { (s, e) -> (s..e).splitRange(cuts) }
        .map { it.first to it.last }
        .filter { (s, e) -> s <= e }
        .toSet().sumOf { (s, e) -> e - s + 1 } - beaconsAlreadyOnTestLine
}

fun part2(input: String): Long {
    val data = input.parseRecords(regex, ::parse).toList()
    val max = if (data.first().first.first == 2L) 20L else 4000000L
    return (0..max).asSequence().map { y ->
        val ranges = data
            .map { (sensor, beacon) ->
                val (sx, sy) = sensor
                val (bx, by) = beacon
                val dist = (sx - bx).absoluteValue + (sy - by).absoluteValue
                val dist10 = dist - (sy - y).absoluteValue
                sx - dist10 to sx + dist10
            }
            .filter { (s, e) -> s <= e }
            .sortedWith(compareBy<Pair<Long, Long>> { it.first }.thenBy { it.second })

        if (ranges.first().first > 0) return@map y to listOf(0L)

        var x = ranges.first().second + 1

        for (r in ranges)
            if (r.first > x) return@map y to listOf(x)
            else if (r.second + 1 > x) x = r.second + 1

        return@map y to emptyList()

    }.first { (_, l) -> l.isNotEmpty() }
        .let { (y, l) -> y + l.single() * 4000000 }
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
    execute(::part2, input)
}

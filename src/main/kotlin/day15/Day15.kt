package day15

import execute
import parseRecords
import readAllText
import kotlin.math.absoluteValue

fun part1(input: String): Int {
    val data = input.parseRecords(regex, ::parse).toList()
    val y = if (data.first().first.first == 2) 10 else 2000000
    val ranges = data
        .map { (sensor, beacon) ->
            val (sx, sy) = sensor
            val (bx, by) = beacon
            val dist = (sx - bx).absoluteValue + (sy - by).absoluteValue
            val dist10 = dist - (sy - y).absoluteValue
            sx - dist10 to sx + dist10
        }
        .filter { (s, e) -> s <= e }

    var size = ranges.sumOf { (s, e) -> e - s + 1 }

    ranges.forEachIndexed { i0, r0 ->
        val (s0, e0) = r0
        ranges.drop(i0 + 1).forEach { r1 ->
            val (s1, e1) = r1
            val common = if (s1 <= s0) {
                if (e1 >= e0) s0..e0
                else if (e1 >= s0) s0..e1
                else IntRange.EMPTY
            } else if (s1 <= e0) {
                if (e1 >= e0) s1..e0
                else s1..e1
            } else IntRange.EMPTY

            println("size $size, $r0 and $r1 have $common common")
            size -= common.last - common.first + 1
        }
    }
    return size
}

fun part2(input: String) = input.parseRecords(regex, ::parse)
    .count()

private val regex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
private fun parse(matchResult: MatchResult) = matchResult.destructured.let { (a, b, c, d) ->
    (a.toInt() to b.toInt()) to (c.toInt() to d.toInt())
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
    execute(::part1, input)
    execute(::part2, test)
    execute(::part2, input)
}

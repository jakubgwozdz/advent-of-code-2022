package day17

import execute
import readAllText
import wtf

val shapes = listOf(
    """
        ....
        ....
        ....
        ####
    """.trimIndent(),
    """
        ....
        .#..
        ###.
        .#..
    """.trimIndent(),
    """
        ....
        ..#.
        ..#.
        ###.
    """.trimIndent(),
    """
        #...
        #...
        #...
        #...
    """.trimIndent(),
    """
        ....
        ....
        ##..
        ##..
    """.trimIndent()
).map { Shape(it) }

fun part1(input: String) = solve(input, 2022L)
fun part2(input: String) = solve(input, 1000000000000L)

class Shape(input: String) {
    private val data = input.lines().also { check(it.size == 4) }.map { it.toCharArray() }.toTypedArray()
    operator fun get(shapeY: Int) = data[shapeY]
}

class Chamber {
    private val data = Array(1000) { (if (it == 0) "+#######+" else "|.......|").toCharArray() }

    //    var offset = 0L
    fun isLineEmpty(i: Long) = this[i].none { it == '#' }
        .also {
            if (it) {
                (1..10).forEach { dy ->
                    (1..7).forEach { dx ->
                        this[i + dy][dx] = '.'
                    }
                }
            }
        }

    operator fun get(i: Long) = data[(i % 1000).toInt()]

    fun rest(shape: Shape, x: Int, y: Long) {
        val chamberY = y + 3
        (0..3).forEach { dY ->
            val sl = shape[dY]
            val cl = this[chamberY - dY]
            (0..3).forEach { dx -> if (sl[dx] == '#') cl[dx + x] = '#' }
        }
    }

    fun canGo(shape: Shape, x: Int, y: Long): Boolean {
        val chamberY = y + 3
        return (0..3).all { dY ->
            val sl = (shape[dY].toInt() shl 5) shr x
            val cl = this[chamberY - dY].toInt()
            sl and cl == 0
        }
    }
}

private fun CharArray.toInt() = fold(0) { acc, c ->
    (acc shl 1) + if (c != '.') 1 else 0
}

private fun solve(input: String, times: Long) = input.trim().let { winds ->
    var windIndex = 0
    val period = winds.length * shapes.count() * 5 * 5 * 7 * 2
    val chamber = Chamber()
    var done = 0L

    var offset = 0L
    var repetitions = 0L
    var prev = 0L

    var height = 0L
    while (done < times) {
        val shape = shapes[(done % shapes.size).toInt()]
        if (done % period == 1000L) {
            offset = height - prev
            prev = height
            if (done / period == 1L) {
                repetitions = (times / period) - (done / period)
                val newDone = done + repetitions * period
                done = newDone
            }
        }
        var x = 3
        var y = height + 4
        var falling = true
        while (falling) {
            val dir = winds[windIndex % winds.length]
            windIndex++
            if (dir == '>') {
                if (chamber.canGo(shape, x + 1, y)) x += 1
            } else if (dir == '<') {
                if (chamber.canGo(shape, x - 1, y)) x -= 1
            } else wtf("$dir")
            if (chamber.canGo(shape, x, y - 1)) y -= 1 else falling = false
        }

        chamber.rest(shape, x, y)
        height = (0..5).map { it + height }.first { chamber.isLineEmpty(it + 1) }

        done++
    }
    height + repetitions * offset
}

fun main() {
    val input = readAllText("local/day17_input.txt")
    val test = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
    execute(::part1, test, 3068)
    execute(::part1, input, 3227)
    execute(::part2, test, 1514285714288)
    execute(::part2, input, 1597714285698)
}

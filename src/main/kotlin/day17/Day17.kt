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
).map { it.lines().map { it.toCharArray() }.toTypedArray() }

fun part1(input: String) = solve(input, 2022L)
fun part2(input: String) = solve(input, 1000000000000L)

class Chamber() {
    val data = Array(1000) { (if (it == 0) "+#######+" else "|.......|").toCharArray() }

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

    fun rest(shape: Array<CharArray>, x: Int, y: Long) {
        val chamberY = y + 3
        shape.forEachIndexed { shapeY, line ->
            line.forEachIndexed { shapeX, c ->
                if (c == '#') this[chamberY - shapeY][shapeX + x] = '#'
            }
        }
    }

    fun canGo(shape: Array<CharArray>, x: Int, y: Long): Boolean {
        val chamberY = y + 3
        return (0..3).all { dy ->
            (0..3).all { dx ->
                shape[dy][dx] == '.' || this[chamberY - dy][x + dx] == '.'
            }
        }
    }
}

private fun solve(input: String, times: Long) = input.trim().let { winds ->
    var windIndex = 0
    val period = winds.length.toLong() * 5 * 350
//    println("period $period")
    val chamber = Chamber()
    var done = 0L

    var p1 = 0L
    var offset = 0L
    var repetitions = 0L
    var prev = 0L

    var height = 0L
    while (done < times) {
        val shape = shapes[(done % shapes.size).toInt()]
        if (done % period == 1000L) {
            offset = height - prev
//            println(offset)
            prev = height
            if (done / period == 1L) {
                repetitions = (times / period) - (done / period)
                val newDone = done + repetitions * period
//                println("repetitions $repetitions, skip to $newDone")
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
//    println("$height + $repetitions * $offset")
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

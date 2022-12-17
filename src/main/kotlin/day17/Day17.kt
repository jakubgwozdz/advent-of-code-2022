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

private fun solve(input: String, times: Long) = input.trim().let { winds ->
    var windIndex = 0
    val rep = (winds.length.toLong() * 3 * 5 * 7).coerceAtMost(times)
    println("rep $rep")
    val chamber = ((0..30000000).map { "|.......|" } + "+#######+").map { it.toCharArray() }.toTypedArray()
    var done = 0L

    var p1 = 0L
    var offset = 0L
    var repetitions = 0L
    var prev = 0L

    var height = 0L
    while (done < times) {
        val shape = shapes[(done % shapes.size).toInt()]
        if (done % rep == 0L) {
            offset = height - prev
            println("step $done, height $height, offset $offset")
            prev = height
            if (height + offset * 2 >= chamber.size) {
                repetitions = (times / rep) - (done / rep)
                val newDone = done + repetitions * rep
                println("repetitions $repetitions, skip to $newDone")
                done = newDone
            }
        }
        var x = 3
        var y = height.toInt() + 4
        var falling = true
        while (falling) {
            val dir = winds[windIndex % winds.length]
            windIndex++
            if (dir == '>') {
                if (canGo(chamber, shape, x + 1, y)) x += 1
            } else if (dir == '<') {
                if (canGo(chamber, shape, x - 1, y)) x -= 1
            } else wtf("$dir")
            if (canGo(chamber, shape, x, y - 1)) y -= 1 else falling = false
        }

        rest(chamber, shape, x, y)
        height = (0..5).map { it + height }.first { chamber[chamber.size - it.toInt() - 2].none { it == '#' } }

        done++
    }
    println("$height + $repetitions * $offset")
    height + repetitions * offset
}

fun rest(chamber: Array<CharArray>, shape: Array<CharArray>, x: Int, y: Int) {
    val chamberY = chamber.size - y - 4
    shape.forEachIndexed { shapeY, line ->
        line.forEachIndexed { shapeX, c ->
            if (c == '#') chamber[chamberY + shapeY][shapeX + x] = '#'
        }
    }
}

fun canGo(chamber: Array<CharArray>, shape: Array<CharArray>, x: Int, y: Int): Boolean {
    val chamberY = chamber.size - y - 4
    return (0..3).all { dy ->
        (0..3).all { dx ->
            shape[dy][dx] == '.' || chamber[chamberY + dy][x + dx] == '.'
        }
    }
}

fun main() {
    val input = readAllText("local/day17_input.txt")
    val test = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
    execute(::part1, test, 3068)
    execute(::part1, input, 3227)
    execute(::part2, test, 1514285714288)
    execute(::part2, input)
}

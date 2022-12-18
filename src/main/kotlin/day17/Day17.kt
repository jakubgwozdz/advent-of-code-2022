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
    private val data = IntArray(4) { input.lines()[it].toCharArray().toInt() }
    operator fun get(shapeY: Int) = data[shapeY]
}

private const val FULL = 511
private const val WALLS = 257

class Chamber {
    private val data = IntArray(80) { if (it == 0) FULL else WALLS }

    //    var offset = 0L
    fun isLineEmpty(i: Long) = this[i] and FULL == WALLS

    fun cleanUpAbove(i: Long) = (1..8).forEach { dy -> this[i + dy] = WALLS }


    private operator fun set(i: Long, value: Int) {
        data[(i % data.size).toInt()] = value
    }

    operator fun get(i: Long) = data[(i % data.size).toInt()]

    fun rest(shape: Shape, x: Int, y: Long) {
        val chamberY = y + 3
        (0..3).forEach { dY ->
            val sl = (shape[dY] shl 5) shr x
            val cl = this[chamberY - dY]
            this[chamberY - dY] = cl or sl
        }
    }

    fun canGo(shape: Shape, x: Int, y: Long): Boolean {
        val chamberY = y + 3
        return (0..3).all { dY ->
            val sl = (shape[dY] shl 5) shr x
            val cl = this[chamberY - dY]
            sl and cl == 0
        }
    }
}

private fun CharArray.toInt() = fold(0) { acc, c ->
    (acc shl 1) + if (c != '.') 1 else 0
}

private fun solve(input: String, times: Long) = input.trim().let { winds ->
    var windIndex = 0
    val chamber = Chamber()
    var done = 0L
    var height = 0L

    var snapshotShapeIdx = 0
    var snapshotWindIdx = 0
    val snapshotChamber = IntArray(40)
    var snapshotHeight = 0L
    val snapshotAt = 1000L
    var period = 0L
    var delta = 0L
    var repetitions = 0L

    while (done < times) {
        val shapeIdx = (done % shapes.size).toInt()
        val shape = shapes[shapeIdx]

        if (done == snapshotAt) {
            snapshotHeight = height
            snapshotShapeIdx = shapeIdx
            snapshotWindIdx = windIndex
            snapshotChamber.indices.forEach { snapshotChamber[it] = chamber[height - it] }
        }
        if (done > snapshotAt && snapshotHeight > 0 && period == 0L) {
            if (snapshotWindIdx == windIndex && snapshotShapeIdx == shapeIdx && snapshotChamber.indices.all { snapshotChamber[it] == chamber[height - it] }) {
                delta = height - snapshotHeight
                period = done - snapshotAt
                repetitions = (times / period) - (done / period) - 1
                done += repetitions * period
            }
        }
        var x = 3
        var y = height + 4
        var falling = true
        while (falling) {
            val dir = winds[windIndex % winds.length]
            windIndex = (windIndex + 1) % winds.length
            if (dir == '>') {
                if (chamber.canGo(shape, x + 1, y)) x += 1
            } else if (dir == '<') {
                if (chamber.canGo(shape, x - 1, y)) x -= 1
            } else wtf("$dir")
            if (chamber.canGo(shape, x, y - 1)) y -= 1 else falling = false
        }

        chamber.rest(shape, x, y)
        height = (0..5).map { it + height }.first { chamber.isLineEmpty(it + 1) }
        chamber.cleanUpAbove(height)

        done++
    }
    height + repetitions * delta
}

fun main() {
    val input = readAllText("local/day17_input.txt")
    val test = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
    execute(::part1, test, 3068)
    execute(::part1, input, 3227)
    execute(::part2, test, 1514285714288)
    execute(::part2, input, 1597714285698)
}

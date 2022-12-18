package day18

import execute
import readAllText

private typealias Cube = Triple<Int, Int, Int>

private val adj = listOf(
    Cube(-1, 0, 0),
    Cube(1, 0, 0),
    Cube(0, -1, 0),
    Cube(0, 1, 0),
    Cube(0, 0, -1),
    Cube(0, 0, 1),
)

private operator fun Cube.plus(other: Cube): Cube =
    Triple(first + other.first, second + other.second, third + other.third)

private fun Cube.isAdjacent(other: Cube) = adj.any { this + it == other }
private fun Cube.isAdjacent(others: Set<Cube>) = others.any { this.isAdjacent(it) }

fun part1(input: String) = parse(input)
    .let { cubes ->
        cubes.sumOf { t ->
            adj.count { t + it !in cubes }
        }
    }

fun part2(input: String) = parse(input)
    .let { cubes ->
        val outsides = cubes.flatMap { t ->
            adj.map { t + it }.filter { it !in cubes }
        }
        val gruppedOutsides = buildMap {
            outsides
                .forEach { c ->
                    addCube(c)
                    adj.map { it + c }.filter { it !in cubes }.forEach { addCube(it) }
                }
        }
            .onEach { (k, v) -> v.retainAll(outsides.toSet()) }
        val outside = gruppedOutsides.maxBy { (k, v) -> v.size }.value
        outsides.count { it in outside }

    }

private var id = 0

private fun MutableMap<Int, MutableSet<Cube>>.addCube(c: Cube) {
    filterValues { c.isAdjacent(it) }.let { found ->
        when (found.size) {
            0 -> this[id++] = mutableSetOf(c)
            1 -> found.values.single() += c
            else -> {
                val s = found.entries.first()
                s.value += c
                found.forEach { (k, v) ->
                    if (k != s.key) {
                        s.value.addAll(v)
                        remove(k)
                    }
                }
            }
        }
    }
}

private fun parse(input: String) = input.lineSequence().filterNot { it.isBlank() }
    .map { it.split(",").map(String::toInt) }
    .map { (x, y, z) -> Cube(x, y, z) }
    .toSet()

fun main() {
    val input = readAllText("local/day18_input.txt")
    val test = """
        2,2,2
        1,2,2
        3,2,2
        2,1,2
        2,3,2
        2,2,1
        2,2,3
        2,2,4
        2,2,6
        1,2,5
        3,2,5
        2,1,5
        2,3,5
    """.trimIndent()
    execute(::part1, test, 64)
    execute(::part1, input, 4400)
    execute(::part2, test, 58)
    execute(::part2, input, 2522)
}

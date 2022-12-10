package day5

import readAllText
import splitBy
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    println(
        part1(
            """
        [D]    
    [N] [C]    
    [Z] [M] [P]
     1   2   3 

    move 1 from 2 to 1
    move 3 from 1 to 3
    move 2 from 2 to 1
    move 1 from 1 to 2
    """.trimIndent()
        )
    )
    println(part1(readAllText("local/day5_input.txt")))
    println(part2(readAllText("local/day5_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

private val regex = "move (\\d+) from (.) to (.)".toRegex()
private fun parse(matchResult: MatchResult) =
    matchResult.destructured.let { (a, b, c) -> Triple(a.toInt(), b.single(), c.single()) }

fun part1(input: String) = solve(input, moveAllAtOnce = false)
fun part2(input: String) = solve(input, moveAllAtOnce = true)

private fun solve(input: String, moveAllAtOnce: Boolean): String {
    val (stacksSection, commands) = input.lineSequence().splitBy(String::isBlank).toList()
    val stacks = parseStacks(stacksSection).toMutableMap()
    commands.map { regex.matchEntire(it)?.let(::parse) ?: error("WTF `$it`") }
        .forEach { (count, from, to) -> stacks.move(from, to, count, moveAllAtOnce) }

    return stacks.toList().sortedBy { it.first }.map { it.second.last() }.joinToString("")
}

private fun parseStacks(stacksSection: List<String>): Map<Char, String> =
    stacksSection.reversed().map { it.chunked(4) }
        .let { parsed ->
            val head = parsed.first()
            val tail = parsed.drop(1)

            head.mapIndexed { index, stack ->
                val stackId = stack.trim().single()
                val stackContent = buildString {
                    tail.map { it[index] }.forEach { if (it.isNotBlank()) append(it[1]) }
                }
                stackId to stackContent
            }
        }
        .toMap()

private fun MutableMap<Char, String>.move(from: Char, to: Char, count: Int, moveAllAtOnce: Boolean) {
    val crates = this[from]!!.takeLast(count)
    this[from] = this[from]!!.dropLast(count)
    this[to] = this[to]!! + if (moveAllAtOnce) crates else crates.reversed()
}

@file:Suppress("USELESS_CAST")

package day2

import readAllText
import wtf
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    println(part1(readAllText("local/day2_input.txt")))
    println(part2(readAllText("local/day2_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

fun part1(input: String) = input.lineSequence().filterNot(String::isBlank).sumOf(::part1result)
fun part2(input: String) = input.lineSequence().filterNot(String::isBlank).sumOf(::part2result)

const val A = 1
const val B = 2
const val C = 3

const val LOSE = 0
const val DRAW = 3
const val WIN = 6

private fun part1result(it: String) = when (it) {
    "A X" -> A + DRAW
    "B X" -> A + LOSE
    "C X" -> A + WIN
    "A Y" -> B + WIN
    "B Y" -> B + DRAW
    "C Y" -> B + LOSE
    "A Z" -> C + LOSE
    "B Z" -> C + WIN
    "C Z" -> C + DRAW
    else -> wtf(it)
}

private fun part2result(it: String) = when (it) {
    "A X" -> C + LOSE
    "B X" -> A + LOSE
    "C X" -> B + LOSE
    "A Y" -> A + DRAW
    "B Y" -> B + DRAW
    "C Y" -> C + DRAW
    "A Z" -> B + WIN
    "B Z" -> C + WIN
    "C Z" -> A + WIN
    else -> wtf(it)
}

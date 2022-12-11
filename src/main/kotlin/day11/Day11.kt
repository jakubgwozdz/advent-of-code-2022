package day11

import execute
import readAllText
import splitBy
import wtf

data class Monkey(
    val id: Int,
    val items: MutableList<Long>,
    val divisor: Long,
    val op: (Long) -> Long,
    val nextMonkeyOp: (Long) -> Int,
)

private fun parseMonkey(lines: List<String>): Monkey {
    val id = lines[0].substringAfter("Monkey ").substringBefore(":").toInt()
    val items = lines[1].substringAfter("Starting items: ").split(", ").map { it.toLong() }.toMutableList()
    val op = lines[2].substringAfter("Operation: new = ").let {
        when {
            it == "old * old" -> { old: Long -> old * old }
            it.startsWith("old + ") -> { old: Long -> old + it.substringAfter("old + ").toLong() }
            it.startsWith("old * ") -> { old: Long -> old * it.substringAfter("old * ").toLong() }
            else -> wtf(it)
        }
    }
    val testDivisor = lines[3].substringAfter("Test: divisible by ").toLong()
    val idOnTrue = lines[4].substringAfter("If true: throw to monkey ").toInt()
    val idOnFalse = lines[5].substringAfter("If false: throw to monkey ").toInt()
    val nextMonkeyOp = { worry: Long -> if (worry % testDivisor == 0L) idOnTrue else idOnFalse }

    return Monkey(id, items, testDivisor, op, nextMonkeyOp)
}

fun part1(input: String) = solve(input, 20, 3)
fun part2(input: String) = solve(input, 10000, 1)

private fun solve(input: String, times: Int, worryDivisor: Long) = input.lineSequence()
    .splitBy(String::isBlank).filter { it.any(String::isNotBlank) }
    .map { parseMonkey(it) }
    .associateBy { it.id }
    .let { monkeys ->
        val common = monkeys.values.map { it.divisor }.reduce { a, b -> a.times(b) }
        val counters = monkeys.keys.associateWith { 0L }.toMutableMap()
        repeat(times) {
            monkeys.toList().sortedBy { it.first }
                .forEach { (_, monkey) ->
                    monkey.items.forEach { item ->
                        val worry = monkey.op(item) % common / worryDivisor
                        val newMonkeyId = monkey.nextMonkeyOp(worry)
                        monkeys[newMonkeyId]!!.items += worry
                        counters[monkey.id] = counters[monkey.id]!! + 1
                    }
                    monkey.items.clear()
                }
        }
        counters.values.sorted().takeLast(2).reduce(Long::times)
    }


fun main() {
    val input = readAllText("local/day11_input.txt")
    val test = """
        Monkey 0:
          Starting items: 79, 98
          Operation: new = old * 19
          Test: divisible by 23
            If true: throw to monkey 2
            If false: throw to monkey 3
        
        Monkey 1:
          Starting items: 54, 65, 75, 74
          Operation: new = old + 6
          Test: divisible by 19
            If true: throw to monkey 2
            If false: throw to monkey 0
        
        Monkey 2:
          Starting items: 79, 60, 97
          Operation: new = old * old
          Test: divisible by 13
            If true: throw to monkey 1
            If false: throw to monkey 3
        
        Monkey 3:
          Starting items: 74
          Operation: new = old + 3
          Test: divisible by 17
            If true: throw to monkey 0
            If false: throw to monkey 1
    """.trimIndent()
    execute(::part1, test, 10605)
    execute(::part1, input, 111210)
    execute(::part2, test, 2713310158)
    execute(::part2, input, 15447387620)
}

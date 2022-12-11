package day11

import execute
import readAllText
import splitBy
import wtf

data class Monkey(
    val id: Int,
    val items: MutableList<Long>,
    val op: (Long) -> Long,
    val nextMonkeyOp: (Long) -> Int,
)

private fun parseMonkey(lines: List<String>): Monkey {
    val id = ("Monkey (\\d+):".toRegex().matchEntire(lines[0]) ?: wtf(lines[0]))
        .destructured.let { (a) -> a.toInt() }
    val items = (" {2}Starting items: (.*)".toRegex().matchEntire(lines[1]) ?: wtf(lines[1]))
        .destructured.let { (a) ->
            a.split(", ").map { it.toLong() }
        }.toMutableList()
    val op = (" {2}Operation: new = (.*)".toRegex().matchEntire(lines[2]) ?: wtf(lines[2]))
        .destructured.let { (a) ->
            when {
                a == "old * old" -> {
                    { old: Long -> old * old }
                }

                a.matches("old \\+ (\\d+)".toRegex()) -> {
                    { old: Long -> old + a.substringAfter("old + ").toLong() }
                }

                a.matches("old \\* (\\d+)".toRegex()) -> {
                    { old: Long -> old * a.substringAfter("old * ").toLong() }
                }

                else -> wtf(a)
            }
        }
    val testDivisor = (" {2}Test: divisible by (\\d+)".toRegex().matchEntire(lines[3]) ?: wtf(lines[3]))
        .destructured.let { (a) -> a.toLong() }
    val idOnTrue = (" {4}If true: throw to monkey (\\d+)".toRegex().matchEntire(lines[4]) ?: wtf(lines[4]))
        .destructured.let { (a) -> a.toInt() }
    val idOnFalse = (" {4}If false: throw to monkey (\\d+)".toRegex().matchEntire(lines[5]) ?: wtf(lines[5]))
        .destructured.let { (a) -> a.toInt() }

    return Monkey(id, items, op) { worry: Long -> if (worry % testDivisor == 0L) idOnTrue else idOnFalse }
}

fun part1(input: String) = input.lineSequence().splitBy(String::isBlank).filter { it.any(String::isNotBlank) }
    .map { parseMonkey(it) }
    .associateBy { it.id }
    .let { monkeys ->
        val counters = monkeys.keys.associateWith { 0L }.toMutableMap()
        repeat(20) {
            monkeys.toList().sortedBy { (k, v) -> k }
                .forEach { (_, monkey) ->
                    monkey.items.forEach { item ->
                        val worry = monkey.op(item) / 3
                        val newMonkeyId = monkey.nextMonkeyOp(worry)
                        monkeys[newMonkeyId]!!.items += worry
                        counters[monkey.id] = counters[monkey.id]!! + 1
                    }
                    monkey.items.clear()
                }
        }
        counters.values.sorted().takeLast(2).reduce(Long::times)
    }

fun part2(input: String) = input.lineSequence().splitBy(String::isBlank).filter { it.any(String::isNotBlank) }
    .map { parseMonkey(it) }
    .associateBy { it.id }
    .let { monkeys ->
        val counters = monkeys.keys.associateWith { 0L }.toMutableMap()
        repeat(10000) {
            monkeys.toList().sortedBy { (k, v) -> k }
                .forEach { (_, monkey) ->
                    monkey.items.forEach { item ->
                        val worry = monkey.op(item)
                        val newMonkeyId = monkey.nextMonkeyOp(worry)
                        monkeys[newMonkeyId]!!.items += worry
                        counters[monkey.id] = counters[monkey.id]!! + 1
                    }
                    monkey.items.clear()
                }
        }
        counters.forEach(::println)
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
    execute(::part2, input)
}

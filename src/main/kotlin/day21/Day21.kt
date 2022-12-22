@file:Suppress("SpellCheckingInspection")

package day21

import execute
import readAllText
import wtf

private val Long.monkey: NumMonkey
    get() = NumMonkey(this)

fun part1(input: String) = input.lineSequence().filterNot { it.isBlank() }
    .map { parse(it) }
    .toMap()
    .let { it.mapValues { (_, desc) -> desc.build(it) } }
    .let { it["root"]!!.calc() }

fun part2(input: String) = input.lineSequence().filterNot { it.isBlank() }
    .map { parse(it) }
    .toMap()
    .let { it + ("humn" to HumnDesc) }
    .let { it.mapValues { (_, desc) -> desc.build(it) } }
    .let { it["root"]!! as OpMonkey }
    .let { root ->
        var (l, r) = root.run { left.flatten() to right.flatten() }
        while (l !is HumnMonkey && r !is HumnMonkey) {
            val (l1, r1) = when {
                l is OpMonkey && r is NumMonkey -> l.inverseTo(r)
                l is NumMonkey && r is OpMonkey -> r.inverseTo(l)
                else -> wtf(l to r)
            }
            val l1f = l1.flatten()
            val r1f = r1.flatten()
            l = l1f
            r = r1f
        }
        if (l is HumnMonkey) r.calc()
        else if (r is HumnMonkey) l.calc()
        else wtf(l to r)
    }

private sealed interface MonkeyDesc {
    fun build(map: Map<String, MonkeyDesc>): Monkey
}

private data class NumDesc(val v: Long) : MonkeyDesc {
    private var cached: Monkey? = null
    override fun build(map: Map<String, MonkeyDesc>) = cached ?: v.monkey.also { cached = it }
}

private data class OpDesc(val leftId: String, val op: String, val rightId: String) : MonkeyDesc {
    private var cached: Monkey? = null
    override fun build(map: Map<String, MonkeyDesc>) =
        cached ?: OpMonkey(map[leftId]!!.build(map), op, map[rightId]!!.build(map)).also { cached = it }
}

private object HumnDesc : MonkeyDesc {
    override fun build(map: Map<String, MonkeyDesc>) = HumnMonkey
}

sealed interface Monkey {
    fun calc(): Long
    fun flatten(): Monkey
}

private data class NumMonkey(val v: Long) : Monkey {
    override fun calc() = v
    override fun flatten(): Monkey = this
    override fun toString() = v.toString()
}

private object HumnMonkey : Monkey {
    override fun calc() = wtf("calc on $this")
    override fun flatten() = this
    override fun toString() = "X"
}

private data class OpMonkey(val left: Monkey, val op: String, val right: Monkey) : Monkey {
    var cached: Long? = null
    var cachedFlatten: Monkey? = null
    override fun calc(): Long {
        val leftCalc = left.calc()
        val rightCalc = right.calc()
        return cached ?: when (op) {
            "+" -> leftCalc + rightCalc
            "-" -> leftCalc - rightCalc
            "*" -> leftCalc * rightCalc
            "/" -> leftCalc / rightCalc
            else -> wtf(this.toString())
        }.also { cached = it }
    }

    override fun flatten(): Monkey {
        val leftFlat = left.flatten()
        val rightFlat = right.flatten()
        return cachedFlatten ?: (
                if (leftFlat is NumMonkey && rightFlat is NumMonkey) calc().monkey
                else OpMonkey(leftFlat, op, rightFlat))
            .also { cachedFlatten = it }
    }

    override fun toString() = "($left$op$right)"
    fun inverseTo(other: NumMonkey): Pair<Monkey, Monkey> {
        val leftToCalc = left.flatten() !is NumMonkey
        val rightToCalc = right.flatten() !is NumMonkey
        return when {
            leftToCalc && op == "+" -> left to OpMonkey(other, "-", right)
            rightToCalc && op == "+" -> right to OpMonkey(other, "-", left)
            leftToCalc && op == "*" -> left to OpMonkey(other, "/", right)
            rightToCalc && op == "*" -> right to OpMonkey(other, "/", left)
            leftToCalc && op == "-" -> left to OpMonkey(other, "+", right)
            rightToCalc && op == "-" -> right to OpMonkey(left, "-", other)
            leftToCalc && op == "/" -> left to OpMonkey(other, "*", right)
            rightToCalc && op == "/" -> right to OpMonkey(left, "/", other)
            else -> wtf(this)
        }
            .let { (l, r) -> l to r }
    }
}

private fun parse(line: String): Pair<String, MonkeyDesc> {
    val (id, desc) = line.split(": ")
    val asNum = desc.toLongOrNull()
    if (asNum != null) return id to NumDesc(asNum)
    val (m1, op, m2) = desc.split(" ")
    return id to OpDesc(m1, op, m2)
}

fun main() {
    val input = readAllText("local/day21_input.txt")
    val test = """
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32
    """.trimIndent()
    execute(::part1, test, 152)
    execute(::part1, input, 286698846151845)
    execute(::part2, test, 301)
    execute(::part2, input, 3759566892641)
}

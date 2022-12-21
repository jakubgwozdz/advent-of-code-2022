package day21

import execute
import readAllText
import wtf

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
    .let { it.mapValues { (_, desc) -> desc.flatten() } }
    .let { it["root"]!! as OpMonkey }
    .let { root ->
        var (l, r) = root.run { m1.flatten() to m2.flatten() }
        while (l !is HumnMonkey && r !is HumnMonkey) {
            val (l1, r1) = when {
                l is OpMonkey && r is NumMonkey -> l.inverseTo(r.v)
                l is NumMonkey && r is OpMonkey -> r.inverseTo(l.v)
                else -> wtf(l to r)
            }
            l = l1
            r = r1
        }
        l to r
    }
    .let { (m1, m2) -> "$m1 = $m2" }

private sealed interface MonkeyDesc {
    fun build(map: Map<String, MonkeyDesc>): Monkey
}

private data class NumDesc(val v: Long) : MonkeyDesc {
    private var cached: Monkey? = null
    override fun build(map: Map<String, MonkeyDesc>) = cached ?: NumMonkey(v).also { cached = it }
}

private data class OpDesc(val m1id: String, val op: String, val m2id: String) : MonkeyDesc {
    private var cached: Monkey? = null
    override fun build(map: Map<String, MonkeyDesc>) =
        cached ?: OpMonkey(map[m1id]!!.build(map), op, map[m2id]!!.build(map)).also { cached = it }
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

private data class OpMonkey(val m1: Monkey, val op: String, val m2: Monkey) : Monkey {
    var cached: Long? = null
    var cachedFlatten: Monkey? = null
    override fun calc(): Long {
        val m1Calc = m1.calc()
        val m2Calc = m2.calc()
        return cached ?: when (op) {
            "+" -> m1Calc + m2Calc
            "-" -> m1Calc - m2Calc
            "*" -> m1Calc * m2Calc
            "/" -> m1Calc / m2Calc
            else -> wtf(this.toString())
        }.also { cached = it }
    }

    override fun flatten(): Monkey {
        val m1Flat = m1.flatten()
        val m2Flat = m2.flatten()
        return cachedFlatten ?: (if (m1Flat is NumMonkey && m2Flat is NumMonkey) NumMonkey(calc()) else OpMonkey(
            m1Flat,
            op,
            m2Flat
        ))
            .also { cachedFlatten = it }
    }

    override fun toString() = "($m1$op$m2)"
    fun inverseTo(calc: Long): Pair<Monkey, Monkey> {
        val m1Flat = m1.flatten()
        val m2Flat = m2.flatten()
        TODO("Not yet implemented")
    }
}

private sealed interface Either<T, U>
private data class Left<T>(val left: T) : Either<T, Nothing>
private data class Right<T>(val right: T) : Either<Nothing, T>

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

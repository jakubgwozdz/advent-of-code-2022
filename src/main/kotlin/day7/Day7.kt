package day7

import readAllText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() = measureTime {
    val example = """
        ${'$'} cd /
        ${'$'} ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        ${'$'} cd a
        ${'$'} ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        ${'$'} cd e
        ${'$'} ls
        584 i
        ${'$'} cd ..
        ${'$'} cd ..
        ${'$'} cd d
        ${'$'} ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent()
    println(part1(example))
    println(part1(readAllText("local/day7_input.txt")))
    println(part2(example))
    println(part2(readAllText("local/day7_input.txt")))
}.let { println(it.toString(DurationUnit.SECONDS, 3)) }

fun part1(input: String) = parse(input).filter { it <= 100000 }.sum()

fun part2(input: String) = parse(input).let { dirSizes ->
    val required = dirSizes.max() - (70000000 - 30000000)
    dirSizes.filter { it >= required }.min()
}

private fun parse(input: String) = input.lineSequence().filterNot(String::isBlank)
    .fold(Filesystem()) { filesystem, command ->
        when {
            command.startsWith("$ cd ") -> filesystem.cd(command)
            command.startsWith("dir ") -> filesystem.dir(command.substringAfter("dir "))
            command.first().isDigit() -> filesystem.file(command.substringBefore(" ").toInt())
            command == "$ ls" -> filesystem.resetCwd()
            else -> error("WTF `$command`")
        }
    }.sizes()

data class Filesystem(
    private val tree: Map<String, DirNode> = mapOf("/" to DirNode()),
    private val cwd: String = "dummy"
) {
    fun cd(command: String) = copy(cwd = cwd.pathResolve(command.substringAfter("$ cd ")))
    fun resetCwd() = apply { tree[cwd]!!.apply { directContent = 0; indirectContent.clear() } }
    fun file(size: Int) = apply { tree[cwd]!!.directContent += size }
    fun dir(dirName: String) = DirNode().let { dirNode ->
        copy(tree = tree + (cwd.pathResolve(dirName) to dirNode))
            .apply { tree[cwd]!!.indirectContent += dirNode }
    }
    fun sizes() = tree.values.map { it.size }

    class DirNode(var directContent: Int = 0, val indirectContent: MutableList<DirNode> = mutableListOf()) {
        val size: Int get() = directContent + indirectContent.sumOf { it.size }
    }

    private fun String.pathResolve(entry: String) = when (entry) {
        ".." -> substringBeforeLast("/").ifBlank { "/" }
        "/" -> "/"
        else -> "$this/$entry"
    }

}

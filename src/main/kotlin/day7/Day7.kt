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

fun part1(input: String) = parse(input).sizes().filter { it <= 100000 }.sum()

fun part2(input: String) = parse(input).let { filesystem ->
    val dirSizes = filesystem.sizes().sorted()
    val required = dirSizes.last() - (70000000 - 30000000)
    val index = dirSizes.binarySearch { it - required }
    dirSizes[if (index >= 0) index else -index - 1]
}

private fun parse(input: String) = input.lineSequence().filterNot(String::isBlank)
    .fold(Filesystem(cwd = "dummy")) { filesystem, command ->
        when {
            command.startsWith("$ cd ") -> filesystem.cd(command)
            command.startsWith("dir ") -> filesystem.addDir(command.substringAfter("dir "))
            command.first().isDigit() -> filesystem.addFile(command.substringBefore(" ").toInt())
            command == "$ ls" -> filesystem.resetCwd()
            else -> error("WTF `$command`")
        }
    }

data class Filesystem(
    private val tree: Map<String, DirNode> = emptyMap(),
    private val cwd: String
) {

    fun cd(command: String) = copy(cwd = cwd.pathResolve(command.substringAfter("$ cd ")))
    fun resetCwd() = copy(tree = tree - cwd)
    fun addFile(size: Int) = copy(tree = tree + (cwd to (tree[cwd] ?: DirNode()).run {
        copy(directContent = directContent + size)
    }))

    fun addDir(dirName: String) = copy(tree = tree + (cwd to (tree[cwd] ?: DirNode()).run {
        copy(indirectContent = indirectContent + cwd.pathResolve(dirName))
    }))

    fun sizes() = tree.values.map { getSize(it) }

    data class DirNode(val directContent: Int = 0, val indirectContent: List<String> = listOf())

    private fun getSize(dirNode: DirNode): Int =
        dirNode.directContent + dirNode.indirectContent.sumOf { this@Filesystem.tree[it]?.let(::getSize) ?: 0 }

    private fun String.pathResolve(entry: String) = when (entry) {
        ".." -> substringBeforeLast("/").ifBlank { "/" }
        "/" -> entry
        else -> if (this == "/") "/$entry" else "$this/$entry"
    }

}

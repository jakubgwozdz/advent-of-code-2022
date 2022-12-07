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

fun part1(input: String): Int = parse(input).filter { it.size <= 100000 }.sumOf { it.size }

fun part2(input: String): Int {
    val dirNodes = parse(input)
    val used = 70000000 - dirNodes.maxOf { it.size }
    val required = 30000000 - used

    return dirNodes.filter { it.size >= required }.minOf { it.size }
}

sealed interface TreeNode {
    val size: Int
}

data class FileNode(override val size: Int) : TreeNode

data class DirNode(val content: MutableList<TreeNode> = mutableListOf()) : TreeNode {
    override val size: Int get() = content.sumOf { it.size }
}

private fun parse(input: String): Collection<DirNode> {
    val filesystem = mutableMapOf("/" to DirNode())

    input.lineSequence().filterNot(String::isBlank)
        .fold("/") { cwd, command ->
            if (command.startsWith("$ cd ")) {
                cwd.pathResolve(command.substringAfter("$ cd "))
            } else {
                if (command.startsWith("dir ")) {
                    val dirNode = DirNode()
                    filesystem[cwd.pathResolve(command.substringAfter("dir "))] = dirNode
                    filesystem[cwd]!!.content += dirNode
                } else if (command.first().isDigit()) {
                    filesystem[cwd]!!.content += FileNode(command.substringBefore(" ").toInt())
                } else require(command == "$ ls") { "WTF `$command`" }
                cwd
            }
        }
    return filesystem.values
}

private fun String.pathResolve(entry: String) =
    when (entry) {
        ".." -> substringBeforeLast("/").let { it.ifBlank { "/" } }
        "/" -> "/"
        else -> "$this/$entry"
    }

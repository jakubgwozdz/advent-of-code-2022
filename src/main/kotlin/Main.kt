import org.intellij.lang.annotations.Language
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() {

    generateKotlinFiles()

    runFor(1, 1) { day1.part1(it) }
    runFor(1, 2) { day1.part2(it) }
    runFor(2, 1) { day2.part1(it) }
    runFor(2, 2) { day2.part2(it) }
    runFor(3, 1) { day3.part1(it) }
    runFor(3, 2) { day3.part2(it) }
    runFor(4, 1) { day4.part1(it) }
    runFor(4, 2) { day4.part2(it) }
    runFor(5, 1) { day5.part1(it) }
    runFor(5, 2) { day5.part2(it) }
    runFor(6, 1) { day6.part1(it) }
    runFor(6, 2) { day6.part2(it) }
    runFor(7, 1) { day7.part1(it) }
    runFor(7, 2) { day7.part2(it) }
    runFor(8, 1) { day8.part1(it) }
    runFor(8, 2) { day8.part2(it) }
    runFor(9, 1) { day9.part1(it) }
    runFor(9, 2) { day9.part2(it) }
    runFor(10, 1) { day10.part1(it) }
    runFor(10, 2) { day10.part2(it) }
    runFor(11, 1) { day11.part1(it) }
    runFor(11, 2) { day11.part2(it) }
    runFor(12, 1) { day12.part1(it) }
    runFor(12, 2) { day12.part2(it) }
    runFor(13, 1) { day13.part1(it) }
    runFor(13, 2) { day13.part2(it) }
    runFor(14, 1) { day14.part1(it) }
    runFor(14, 2) { day14.part2(it) }
    runFor(15, 1) { day15.part1(it) }
    runFor(15, 2) { day15.part2(it) }
    runFor(16, 1) { day16.part1(it) }
    runFor(16, 2) { day16.part2(it) }
    runFor(17, 1) { day17.part1(it) }
    runFor(17, 2) { day17.part2(it) }
    runFor(18, 1) { day18.part1(it) }
    runFor(18, 2) { day18.part2(it) }
    runFor(19, 1) { day19.part1(it) }
    runFor(19, 2) { day19.part2(it) }
    runFor(20, 1) { day20.part1(it) }
    runFor(20, 2) { day20.part2(it) }
    runFor(21, 1) { day21.part1(it) }
    runFor(21, 2) { day21.part2(it) }
    runFor(22, 1) { day22.part1(it) }
    runFor(22, 2) { day22.part2(it) }
    runFor(23, 1) { day23.part1(it) }
    runFor(23, 2) { day23.part2(it) }
    runFor(24, 1) { day24.part1(it) }
    runFor(24, 2) { day24.part2(it) }
    runFor(25, 1) { day25.part1(it) }
    runFor(25, 2) { day25.part2(it) }
}

private fun runFor(day: Int, part: Int, op: (String) -> Any) {
    val file = "local/day${day}_input.txt"
    if (Files.exists(Path.of(file))) {
        val input = readAllText(file)
        measureTime {
            print("Day $day part $part: ")
            val result = try {
                op(input)
            } catch (e: Exception) {
                e.toString()
            }
            print(result)
        }.also { println(" in " + it.toString(DurationUnit.SECONDS, 3)) }
    }
}

private fun generateKotlinFiles() {
    @Language("kotlin") val content = """
        package day0
        
        import parseRecords
        import readAllText
        import kotlin.time.DurationUnit
        import kotlin.time.measureTime
        
        fun main() = measureTime {
            println(part1(readAllText("local/day0_input.txt")))
            println(part2(readAllText("local/day0_input.txt")))
        }.let { println(it.toString(DurationUnit.SECONDS, 3)) }
        
        private val regex = "(.+)".toRegex()
        private fun parse(matchResult: MatchResult) = matchResult.destructured.let { (a) -> a }
        
        fun part1(input: String) = input.parseRecords(regex, ::parse)
            .count()
        
        fun part2(input: String) = input.parseRecords(regex, ::parse)
            .count()

    """.trimIndent()


    (1..25).forEach { day ->
        val srcPath = Path.of("src/main/kotlin/day${day}/Day${day}.kt")
        val inputPath = Path.of("local/day${day}_input.txt")

        if (!Files.exists(inputPath)) {
            Files.createDirectories(srcPath.parent)
            Files.writeString(srcPath, content.replace("day0", "day$day"))
        }
    }
}


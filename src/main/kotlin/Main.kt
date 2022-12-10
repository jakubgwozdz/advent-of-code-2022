import org.intellij.lang.annotations.Language
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import day1.part1 as day1part1
import day1.part2 as day1part2
import day10.part1 as day10part1
import day10.part2 as day10part2
import day11.part1 as day11part1
import day11.part2 as day11part2
import day12.part1 as day12part1
import day12.part2 as day12part2
import day13.part1 as day13part1
import day13.part2 as day13part2
import day14.part1 as day14part1
import day14.part2 as day14part2
import day15.part1 as day15part1
import day15.part2 as day15part2
import day16.part1 as day16part1
import day16.part2 as day16part2
import day17.part1 as day17part1
import day17.part2 as day17part2
import day18.part1 as day18part1
import day18.part2 as day18part2
import day19.part1 as day19part1
import day19.part2 as day19part2
import day2.part1 as day2part1
import day2.part2 as day2part2
import day20.part1 as day20part1
import day20.part2 as day20part2
import day21.part1 as day21part1
import day21.part2 as day21part2
import day22.part1 as day22part1
import day22.part2 as day22part2
import day23.part1 as day23part1
import day23.part2 as day23part2
import day24.part1 as day24part1
import day24.part2 as day24part2
import day25.part1 as day25part1
import day3.part1 as day3part1
import day3.part2 as day3part2
import day4.part1 as day4part1
import day4.part2 as day4part2
import day5.part1 as day5part1
import day5.part2 as day5part2
import day6.part1 as day6part1
import day6.part2 as day6part2
import day7.part1 as day7part1
import day7.part2 as day7part2
import day8.part1 as day8part1
import day8.part2 as day8part2
import day9.part1 as day9part1
import day9.part2 as day9part2

fun main() {
    generateKotlinFiles()
    benchmark()
}

fun benchmark() {
    runFor("local/day1_input.txt", ::day1part1, ::day1part2)
    runFor("local/day2_input.txt", ::day2part1, ::day2part2)
    runFor("local/day3_input.txt", ::day3part1, ::day3part2)
    runFor("local/day4_input.txt", ::day4part1, ::day4part2)
    runFor("local/day5_input.txt", ::day5part1, ::day5part2)
    runFor("local/day6_input.txt", ::day6part1, ::day6part2)
    runFor("local/day7_input.txt", ::day7part1, ::day7part2)
    runFor("local/day8_input.txt", ::day8part1, ::day8part2)
    runFor("local/day9_input.txt", ::day9part1, ::day9part2)
    runFor("local/day10_input.txt", ::day10part1, ::day10part2)
    runFor("local/day11_input.txt", ::day11part1, ::day11part2)
    runFor("local/day12_input.txt", ::day12part1, ::day12part2)
    runFor("local/day13_input.txt", ::day13part1, ::day13part2)
    runFor("local/day14_input.txt", ::day14part1, ::day14part2)
    runFor("local/day15_input.txt", ::day15part1, ::day15part2)
    runFor("local/day16_input.txt", ::day16part1, ::day16part2)
    runFor("local/day17_input.txt", ::day17part1, ::day17part2)
    runFor("local/day18_input.txt", ::day18part1, ::day18part2)
    runFor("local/day19_input.txt", ::day19part1, ::day19part2)
    runFor("local/day20_input.txt", ::day20part1, ::day20part2)
    runFor("local/day21_input.txt", ::day21part1, ::day21part2)
    runFor("local/day22_input.txt", ::day22part1, ::day22part2)
    runFor("local/day23_input.txt", ::day23part1, ::day23part2)
    runFor("local/day24_input.txt", ::day24part1, ::day24part2)
    runFor("local/day25_input.txt", ::day25part1)
}

private fun runFor(filename: String, vararg ops: (String) -> Any) {
    val file = filename
    if (Files.exists(Path.of(file))) {
        val input = readAllText(file)
        ops.forEach { op ->
            val mark = TimeSource.Monotonic.markNow()
            while (mark.elapsedNow() < 1.seconds) op(input)
            execute(op, input)
        }
    }
}

private fun generateKotlinFiles() {
    @Language("kotlin") val content = """
        package day0
        
        import parseRecords
        import readAllText
        import execute
                
        fun part1(input: String) = input.parseRecords(regex, ::parse)
            .count()
        
        fun part2(input: String) = input.parseRecords(regex, ::parse)
            .count()
        
        private val regex = "(.+)".toRegex()
        private fun parse(matchResult: MatchResult) = matchResult.destructured.let { (a) -> a }
        
        fun main() {
            val input = readAllText("local/day0_input.txt")
            val test = ""${'"'}
                
            ""${'"'}.trimIndent()
            execute(::part1, test, )
            execute(::part1, input, )
            execute(::part2, test, )
            execute(::part2, input, )
        }

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


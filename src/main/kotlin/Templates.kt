import org.intellij.lang.annotations.Language
import java.nio.file.Files
import java.nio.file.Path

fun main() {
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


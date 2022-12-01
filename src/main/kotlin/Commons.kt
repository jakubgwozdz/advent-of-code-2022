import java.nio.file.Files
import java.nio.file.Path

fun readAllText(filePath: String): String = Files.readString(Path.of(filePath))

fun <R> String.parseRecords(regex: Regex, op: (MatchResult) -> R): Sequence<R> =
    lineSequence().filterNot(String::isBlank)
        .map { regex.matchEntire(it) ?: error("WTF `$it`") }
        .map(op)

fun <R> String.parseRecords(op: (String) -> R): Sequence<R> =
    lineSequence().filterNot(String::isBlank)
        .map(op)

fun <T> Sequence<T>.splitBy(op: (T) -> Boolean): Sequence<List<T>> = with(iterator()) {
    sequence {
        val buffer = mutableListOf<T>()
        while (this@with.hasNext()) {
            val entry = next()
            if (op(entry)) {
                yield(buffer.toList())
                buffer.clear()
            } else {
                buffer.add(entry)
            }
        }
        yield(buffer)
    }
}

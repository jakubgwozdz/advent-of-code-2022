import java.nio.file.Files
import java.nio.file.Path
import kotlin.jvm.internal.FunctionReference
import kotlin.jvm.internal.PackageReference
import kotlin.time.DurationUnit
import kotlin.time.TimeSource.Monotonic.markNow

fun readAllText(filePath: String): String = Files.readString(Path.of(filePath))

fun <R> String.parseRecords(regex: Regex, op: (MatchResult) -> R): Sequence<R> =
    lineSequence().filterNot(String::isBlank)
        .map { regex.matchEntire(it) ?: wtf(it) }
        .map(op)

fun <R> String.parseRecords(op: (String) -> R): Sequence<R> =
    lineSequence().filterNot(String::isBlank)
        .map(op)

fun <T> String.parse(regex: Regex, op: (MatchResult.Destructured) -> T): T =
    (regex.matchEntire(this) ?: wtf(this)).destructured.let(op)


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

@Suppress("NOTHING_TO_INLINE")
inline fun <T> execute(noinline op: (String) -> T, input: String, expected: T? = null) {
    val mark = markNow()
    val result = try {
        op(input).also { if (expected != null) check(it == expected) { "Expected `$expected`, got `$it`" } }
            .toString()
    } catch (e: Exception) {
        e.printStackTrace()
        e.toString()
    }
    val duration = mark.elapsedNow()
    val code = (op as FunctionReference).let { "${(it.owner as PackageReference).jClass.packageName}.${it.name}()" }
    if (result.contains('\n'))
        println("$code after ${duration.toString(DurationUnit.MILLISECONDS, 3)} => \n${result.trimEnd()}")
    else
        println("$code after ${duration.toString(DurationUnit.MILLISECONDS, 3)} => $result")
}

@Suppress("NOTHING_TO_INLINE")
inline fun wtf(a: Any): Nothing = error("WTF `$a`")

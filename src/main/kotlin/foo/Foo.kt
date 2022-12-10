package foo

import kotlin.jvm.internal.FunctionReference
import kotlin.jvm.internal.PackageReference
import bar.baz as barbaz

fun xd(op: (Int) -> Int) {
    val code = (op as FunctionReference).let { (it.owner as PackageReference).jClass.packageName + "." + it.name }
    println("calling $code(4) and getting ${op(4)}")
}

fun main() {
    xd(::barbaz)
}

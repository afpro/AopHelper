package net.afpro.idea.aophelper.base

import com.intellij.psi.PsiPrimitiveType

val Boolean.asInt: Int
    get() = if (this) 1 else 0

fun PsiPrimitiveType.binaryName(): String {
    return when (name) {
        "byte" -> "B"
        "char" -> "C"
        "double" -> "D"
        "float" -> "F"
        "int" -> "I"
        "long" -> "J"
        "short" -> "S"
        "boolean" -> "Z"
        "void" -> "V"
        else -> name
    }
}

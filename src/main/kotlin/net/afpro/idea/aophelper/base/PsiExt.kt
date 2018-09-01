package net.afpro.idea.aophelper.base

import com.intellij.psi.PsiAnnotation

fun Array<out PsiAnnotation?>.findAnnotation(name: String): PsiAnnotation?
    = firstOrNull { it?.nameReferenceElement?.reference?.canonicalText == name }


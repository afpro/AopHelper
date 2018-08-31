package net.afpro.idea.aophelper.java

import com.intellij.psi.PsiElement
import net.afpro.idea.aophelper.base.AopSourceLineMarkerProvider


class LancetJavaAopSourceLineMarkerProvider : AopSourceLineMarkerProvider() {
    override fun aopType(element: PsiElement, allowSlow: Boolean): ElementAopType {
        return ElementAopType.NOBODY
    }

    override fun aopAnchor(element: PsiElement): PsiElement {
        return element
    }

    override fun aopTargets(element: PsiElement): Sequence<PsiElement> {
        return emptySequence()
    }
}
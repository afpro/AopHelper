package net.afpro.idea.aophelper.java

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import net.afpro.idea.aophelper.base.AopSourceLineMarkerProvider


class LancetJavaAopSourceLineMarkerProvider : AopSourceLineMarkerProvider() {
    override fun aopType(element: PsiElement, allowSlow: Boolean): ElementAopType {
        if (element is PsiMethod) {
            val annotations = element.annotations
            annotations.forEach { a ->
                val ref = a.nameReferenceElement?.reference ?: return@forEach
                if (ref.canonicalText == "me.ele.lancet.base.annotations.TargetClass") {
                    return ElementAopType.SOURCE
                }
            }
        }

        return ElementAopType.NOBODY
    }

    override fun aopAnchor(element: PsiElement): PsiElement {
        return (element as PsiMethod).nameIdentifier!!
    }

    override fun aopTargets(element: PsiElement): Sequence<PsiElement> {
        return emptySequence()
    }
}
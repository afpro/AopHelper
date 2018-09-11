package net.afpro.idea.aophelper.lancet

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import net.afpro.idea.aophelper.base.AopSourceLineMarkerProvider
import net.afpro.idea.aophelper.base.findAnnotation
import net.afpro.idea.aophelper.base.hasRefInt


class LancetJavaAopSourceLineMarkerProvider : AopSourceLineMarkerProvider<LancetAopType>() {
    override fun aopType(element: PsiElement, allowSlow: Boolean): LancetAopType {
        if (!allowSlow)
            return LancetAopType.NOBODY

        if (element !is PsiMethod)
            return LancetAopType.NOBODY

        val annotations = element.annotations
        if (annotations.isEmpty())
            return LancetAopType.NOBODY

        val targetClass = annotations.findAnnotation(cnTargetClass)
        val implInterface = annotations.findAnnotation(cnImplInterface)
        if (targetClass.hasRefInt + implInterface.hasRefInt != 1) // only 1 non-null
            return LancetAopType.NOBODY

        val nameRegex = annotations.findAnnotation(cnNameRegex)
        val insert = annotations.findAnnotation(cnInsert)
        if (nameRegex != null && insert != null)
            return LancetAopType.NOBODY

        return LancetAopType(true, true,
                method = element,
                targetClass = targetClass,
                implInterface = implInterface,
                nameRegex = nameRegex,
                insert = insert,
                proxy = annotations.findAnnotation(cnProxy),
                tryCatchHandler = annotations.findAnnotation(cnTryCatchHandler))
    }

    override fun aopAnchor(element: PsiElement, type: LancetAopType): PsiElement {
        return (element as? PsiMethod)?.nameIdentifier ?: super.aopAnchor(element, type)
    }

    override fun aopTargets(element: PsiElement, type: LancetAopType): Sequence<PsiElement> {
        return LancetSourceSearcher.createQuery(type)
                .asSequence()
                .flatten()
    }
}
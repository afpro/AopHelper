package net.afpro.idea.aophelper.java.lancet

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import net.afpro.idea.aophelper.base.AopSourceLineMarkerProvider
import net.afpro.idea.aophelper.base.findAnnotation


class LancetJavaAopSourceLineMarkerProvider : AopSourceLineMarkerProvider<LancetJavaAopSourceLineMarkerProvider.MyAopType>() {
    override fun aopType(element: PsiElement, allowSlow: Boolean): MyAopType {
        if (!allowSlow)
            return NOBODY

        if (element !is PsiMethod)
            return NOBODY

        val annotations = element.annotations
        if (annotations.isEmpty())
            return NOBODY

        val targetClass = annotations.findAnnotation("me.ele.lancet.base.annotations.TargetClass")
        val nameRegex = annotations.findAnnotation("me.ele.lancet.base.annotations.NameRegex")
        val implInterface = annotations.findAnnotation("me.ele.lancet.base.annotations.ImplementedInterface")

        if (sequenceOf(targetClass, nameRegex, implInterface).filterNotNull().count() != 1)
            return NOBODY

        val type = MyAopType(true, true)
        type.targetClass = targetClass
        type.nameRegex = nameRegex
        type.implInterface = implInterface
        return type
    }

    override fun aopAnchor(element: PsiElement, type: MyAopType): PsiElement {
        return (element as PsiMethod).nameIdentifier!!
    }

    override fun aopTargets(element: PsiElement, type: MyAopType): Sequence<PsiElement> {
        return emptySequence()
    }

    class MyAopType(override val hasAop: Boolean, override val isSlow: Boolean) : AopSourceLineMarkerProvider.IAopType {
        var targetClass: PsiAnnotation? = null
        var implInterface: PsiAnnotation? = null
        var nameRegex: PsiAnnotation? = null
    }

    companion object {
        val NOBODY = MyAopType(false, false)
    }
}
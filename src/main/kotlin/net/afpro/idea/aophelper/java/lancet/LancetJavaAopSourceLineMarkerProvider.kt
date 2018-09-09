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
        val implInterface = annotations.findAnnotation("me.ele.lancet.base.annotations.ImplementedInterface")
        if (sequenceOf(targetClass, implInterface).filterNotNull().count() != 1)
            return NOBODY

        val nameRegex = annotations.findAnnotation("me.ele.lancet.base.annotations.NameRegex")
        val insert = annotations.findAnnotation("me.ele.lancet.base.annotations.Insert")
        if (nameRegex != null && insert != null)
            return NOBODY

        return MyAopType(true, true,
                targetClass = targetClass,
                implInterface = implInterface,
                nameRegex = nameRegex,
                insert = insert,
                proxy = annotations.findAnnotation("me.ele.lancet.base.annotations.Proxy"),
                tryCatchHandler = annotations.findAnnotation("me.ele.lancet.base.annotations.TryCatchHandler"))
    }

    override fun aopAnchor(element: PsiElement, type: MyAopType): PsiElement {
        return (element as PsiMethod).nameIdentifier!!
    }

    override fun aopTargets(element: PsiElement, type: MyAopType): Sequence<PsiElement> {
        element as PsiMethod

        var result: Sequence<PsiElement> = emptySequence()

        // find type.method sequence
        when {
            type.targetClass != null -> {
            }
            type.implInterface != null -> {
            }
        }

        // filter by name regex
        if (type.nameRegex != null) {
            val regex = type.nameRegex.findAttributeValue("value")
        }

        return result
    }

    class MyAopType(override val hasAop: Boolean, override val isSlow: Boolean,
                    val targetClass: PsiAnnotation? = null,
                    val implInterface: PsiAnnotation? = null,
                    val nameRegex: PsiAnnotation? = null,
                    val insert: PsiAnnotation? = null,
                    val proxy: PsiAnnotation? = null,
                    val tryCatchHandler: PsiAnnotation? = null)
        : AopSourceLineMarkerProvider.IAopType

    companion object {
        val NOBODY = MyAopType(false, false)
    }
}
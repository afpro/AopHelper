package net.afpro.idea.aophelper.lancet

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiMethod
import net.afpro.idea.aophelper.base.AopSourceLineMarkerProvider

class LancetAopType (override val hasAop: Boolean, override val isSlow: Boolean,
                     val method: PsiMethod? = null,
                     val targetClass: PsiAnnotation? = null,
                     val implInterface: PsiAnnotation? = null,
                     val nameRegex: PsiAnnotation? = null,
                     val insert: PsiAnnotation? = null,
                     val proxy: PsiAnnotation? = null,
                     val tryCatchHandler: PsiAnnotation? = null)
    : AopSourceLineMarkerProvider.IAopType {
    companion object {
        val NOBODY = LancetAopType(false, false)
    }
}

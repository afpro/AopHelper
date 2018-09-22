package net.afpro.idea.aophelper.lancet

import com.intellij.patterns.PsiJavaPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext

class LancetJavaPsiReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
                PsiJavaPatterns.psiAnnotation(),
                Provider())
    }

    private class Provider : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            val annotation = element as? PsiAnnotation ?: return emptyArray()
            return emptyArray()
        }
    }
}

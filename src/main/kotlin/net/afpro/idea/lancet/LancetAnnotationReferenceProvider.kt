package net.afpro.idea.lancet

import com.intellij.openapi.paths.PathReference
import com.intellij.openapi.paths.PathReferenceProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference


class LancetAnnotationReferenceProvider : PathReferenceProvider {
    override fun createReferences(psiElement: PsiElement, references: MutableList<PsiReference>, soft: Boolean): Boolean {
        return false
    }

    override fun getPathReference(path: String, element: PsiElement): PathReference? {
        return null
    }
}
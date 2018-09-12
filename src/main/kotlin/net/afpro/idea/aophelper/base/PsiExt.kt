package net.afpro.idea.aophelper.base

import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import javax.swing.Icon

/**
 * find annotation of name {@param name} or null
 *
 * @param name annotation name
 */
fun Array<out PsiAnnotation?>.findAnnotation(name: String): PsiAnnotation?
    = firstOrNull { it?.nameReferenceElement?.reference?.canonicalText == name }

/**
 * create line marker on element
 *
 * @param icon icon
 * @param targets marker navigate targets
 */
fun PsiElement.mark(icon: Icon, targets: Collection<PsiElement> = emptyList()): LineMarkerInfo<PsiElement> {
    return if (targets.isEmpty()) {
        LineMarkerInfo(
                this,
                this.textRange,
                icon,
                Pass.LINE_MARKERS,
                null,
                null,
                GutterIconRenderer.Alignment.LEFT)
    } else {
        NavigationGutterIconBuilder.create(icon)
                .setTargets(targets)
                .createLineMarkerInfo(this)
    }
}

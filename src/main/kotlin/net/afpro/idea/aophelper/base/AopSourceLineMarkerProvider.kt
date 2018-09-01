package net.afpro.idea.aophelper.base

import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.util.ui.ColorIcon
import java.awt.Color
import javax.swing.Icon


abstract class AopSourceLineMarkerProvider<T : AopSourceLineMarkerProvider.IAopType> : LineMarkerProvider {
    final override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val type = aopType(element, false)
        if (type.hasAop && !type.isSlow) {
            return createLineMarker(element, type)
        }
        return null
    }

    final override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        elements.asSequence()
                .map {
                    val type = aopType(it, true)
                    if (type.hasAop && type.isSlow) {
                        createLineMarker(it, type)
                    } else {
                        null
                    }
                }
                .filterNotNull()
                .forEach { result.add(it) }
    }

    private fun createLineMarker(element: PsiElement, type: T): LineMarkerInfo<PsiElement>? {
        val anchor = aopAnchor(element, type)
        val targets = aopTargets(element, type).toList()

        return if (targets.isEmpty()) {
            LineMarkerInfo(
                    anchor,
                    anchor.textRange,
                    createIcon(false),
                    Pass.LINE_MARKERS,
                    null,
                    null,
                    GutterIconRenderer.Alignment.LEFT)
        } else {
            NavigationGutterIconBuilder.create(createIcon(true))
                    .setTargets(targets)
                    .createLineMarkerInfo(anchor)
        }
    }

    protected open fun createIcon(hasNav: Boolean): Icon {
        return ColorIcon(12, Color(0x33, 0x66, 0x99))
    }

    protected abstract fun aopType(element: PsiElement, allowSlow: Boolean): T

    protected open fun aopAnchor(element: PsiElement, type: T): PsiElement {
        return element
    }

    protected open fun aopTargets(element: PsiElement, type: T): Sequence<PsiElement> {
        return emptySequence()
    }

    interface IAopType {
        val hasAop: Boolean
        val isSlow: Boolean
    }
}
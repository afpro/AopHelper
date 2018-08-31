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


abstract class AopSourceLineMarkerProvider : LineMarkerProvider {
    final override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (aopType(element, false) == ElementAopType.SOURCE) {
            return createLineMarker(element)
        }
        return null
    }

    final override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        elements.asSequence()
                .filter { aopType(it, true) == ElementAopType.SLOW_SOURCE }
                .mapNotNull(this::createLineMarker)
                .forEach { result.add(it) }
    }

    private fun createLineMarker(element: PsiElement): LineMarkerInfo<PsiElement>? {
        val anchor = aopAnchor(element)
        val targets = aopTargets(element).toList()
        val icon = createIcon(targets.isNotEmpty())

        return if (targets.isEmpty()) {
            LineMarkerInfo(
                    anchor,
                    anchor.textRange,
                    icon,
                    Pass.LINE_MARKERS,
                    null,
                    null,
                    GutterIconRenderer.Alignment.LEFT)
        } else {
            NavigationGutterIconBuilder.create(icon)
                    .setTargets(targets)
                    .createLineMarkerInfo(anchor)
        }
    }

    protected open fun createIcon(hasNav: Boolean): Icon {
        return ColorIcon(12, Color(0x33, 0x66, 0x99))
    }

    protected abstract fun aopType(element: PsiElement, allowSlow: Boolean): ElementAopType

    protected open fun aopAnchor(element: PsiElement): PsiElement {
        return element
    }

    protected open fun aopTargets(element: PsiElement): Sequence<PsiElement> {
        return emptySequence()
    }

    protected enum class ElementAopType {
        NOBODY,
        SOURCE,
        SLOW_SOURCE,
    }
}
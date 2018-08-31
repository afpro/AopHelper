package net.afpro.idea.aophelper.base

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
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
                .filter { aopType(it, true) != ElementAopType.NOBODY }
                .mapNotNull(this::createLineMarker)
                .forEach { result.add(it) }
    }

    private fun createLineMarker(element: PsiElement) : LineMarkerInfo<PsiElement>? {
        val targets = aopTargets(element).toList()
        if (targets.isEmpty())
            return null

        return NavigationGutterIconBuilder.create(createIcon())
                .setTargets(targets)
                .createLineMarkerInfo(aopAnchor(element))
    }

    protected open fun createIcon(): Icon {
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
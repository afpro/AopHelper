package net.afpro.idea.aophelper.lancet

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import net.afpro.idea.aophelper.base.mark

interface LancetLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return null
    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        LancetSearch.search(elements.asSequence().filterIsInstance<PsiClass>(),
                onTargetFound = { method, source ->
                    method.nameIdentifier
                            ?.mark(getMarkIcon(false), source)
                            ?.also { result.add(it) }
                },
                onSourceFound = { method, targets ->
                    method.nameIdentifier
                            ?.mark(getMarkIcon(true, !targets.isEmpty()))
                            ?.also { result.add(it) }
                })
    }
}

class LancetJavaAopSourceLineMarkerProvider : LancetLineMarkerProvider
class LancetKotlinAopSourceLineMarkerProvider : LancetLineMarkerProvider

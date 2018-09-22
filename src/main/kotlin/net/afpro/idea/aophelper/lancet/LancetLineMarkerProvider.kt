package net.afpro.idea.aophelper.lancet

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.psi.util.PsiUtil
import net.afpro.idea.aophelper.base.asMethod
import net.afpro.idea.aophelper.base.findClassByName
import net.afpro.idea.aophelper.base.mark
import org.jetbrains.kotlin.idea.search.allScope
import kotlin.coroutines.experimental.buildSequence

class LancetLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return null
    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        val typesMap = HashMap<Project, LancetTypes>()

        elements.asSequence()
                .map { it.asMethod() }
                .filterNotNull()
                .flatMap {
                    val proj = PsiUtil.getProjectInReadAction(it)
                    collectSlowLineMarkers(it, typesMap.getOrPut(proj) { LancetTypes(proj) })
                }
                .filterNotNull()
                .forEach {
                    result.add(it)
                }
    }

    private fun collectSlowLineMarkers(method: PsiMethod, types: LancetTypes): Sequence<LineMarkerInfo<PsiElement>?> = buildSequence {
        val asInjectPoint = types.possibleInjectPoints[method]
        if (asInjectPoint != null) {
            yield(markAnchorOf(method).mark(
                    LancetIcon.InjectPoint.icon,
                    asInjectPoint.findAllTargets()
                            .asSequence()
                            .map { it.nameIdentifier }
                            .filterNotNull()
                            .toList()
            ))
        }

        // as a target
        val injectPointsToThis = types.possibleInjectPoints.values.asSequence()
                .filter { it.match(method) }
                .toList()
        if (!injectPointsToThis.isEmpty()) {
            yield(markAnchorOf(method).mark(
                    LancetIcon.TargetPoint.icon,
                    injectPointsToThis
                            .asSequence()
                            .map { it.injectMethod.nameIdentifier }
                            .filterNotNull()
                            .toList()
            ))
        }
    }

    companion object {
        private fun markAnchorOf(method: PsiMethod): PsiElement {
            return method.nameIdentifier ?: method
        }
    }
}

private data class LancetTypes(
        val proj: Project,
        val scope: GlobalSearchScope = proj.allScope(),
        val cTargetClass: PsiClass? = findClassByName(cnTargetClass, proj, scope),
        val cImplInterface: PsiClass? = findClassByName(cnImplInterface, proj, scope),
        val cNameRegex: PsiClass? = findClassByName(cnNameRegex, proj, scope),
        val cInsert: PsiClass? = findClassByName(cnInsert, proj, scope),
        val cProxy: PsiClass? = findClassByName(cnProxy, proj, scope),
        val cTryCatchHandler: PsiClass? = findClassByName(cnTryCatchHandler, proj, scope)) {
    val possibleInjectPoints by lazy(LazyThreadSafetyMode.NONE) {
        possibleMethods()
                .map(this::methodToInjectPoint)
                .filterNotNull()
                .toMap()
    }

    private fun possibleMethods(): Sequence<PsiMethod> = buildSequence {
        if (cTargetClass != null) {
            yieldAll(AnnotatedElementsSearch.searchPsiMethods(cTargetClass, scope))
        }
        if (cImplInterface != null) {
            yieldAll(AnnotatedElementsSearch.searchPsiMethods(cImplInterface, scope))
        }
    }

    private fun methodToInjectPoint(method: PsiMethod): Pair<PsiMethod, LancetInfo>? {
        val lancetInfo = LancetInfo.get(method) ?: return null
        return method to lancetInfo
    }
}

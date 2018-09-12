package net.afpro.idea.aophelper.lancet

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import net.afpro.idea.aophelper.base.findClassByName
import net.afpro.idea.aophelper.base.fitName
import org.jetbrains.kotlin.idea.search.allScope
import kotlin.coroutines.experimental.buildSequence

internal class LancetSearch {
    private val dataMap = HashMap<Project, Data>()
    private val sourceResult = HashMap<PsiMethod, MutableList<PsiMethod>>()
    private val targetResult = HashMap<PsiMethod, MutableList<PsiMethod>>()

    private fun search(types: Sequence<PsiClass>,
                       onTargetFound: (PsiMethod, List<PsiMethod>) -> Unit,
                       onSourceFound: (PsiMethod, List<PsiMethod>) -> Unit) {
        types.forEach { search(it) }
        sourceResult.forEach(onSourceFound)
        targetResult.forEach(onTargetFound)
    }

    private fun search(type: PsiClass) {
        val proj = type.project
        val data = dataMap.getOrCreate(proj)
        data.sourceMethods.forEach { _, source ->
            checkSourceMethod(type, source)
            checkTargetMethod(type, source)
        }
    }

    private fun checkSourceMethod(type: PsiClass, source: SourceMethod) {
        if (source.method.containingClass != type)
            return
    }

    private fun checkTargetMethod(type: PsiClass, source: SourceMethod) {
    }

    companion object {
        fun search(types: Sequence<PsiClass>,
                   onTargetFound: (PsiMethod, List<PsiMethod>) -> Unit,
                   onSourceFound: (PsiMethod, List<PsiMethod>) -> Unit) {
            LancetSearch().search(types, onTargetFound, onSourceFound)
        }

        private fun MutableMap<Project, Data>.getOrCreate(proj: Project): Data {
            return getOrPut(proj) { Data(proj, proj.allScope()) }
        }

        private fun MutableMap<PsiMethod, MutableList<PsiMethod>>.append(key: PsiMethod, value: PsiMethod) {
            getOrPut(key) { ArrayList() }.add(value)
        }
    }

    private class Data(
            val proj: Project,
            val scope: GlobalSearchScope,
            val cTargetClass: PsiClass? = findClassByName(cnTargetClass, proj, scope),
            val cImplInterface: PsiClass? = findClassByName(cnImplInterface, proj, scope),
            val sourceMethods: MutableMap<PsiMethod, SourceMethod> = HashMap()) {

        init {
            searchSourceMethods()
        }

        private fun searchSourceMethods() {
            sourceAnnotatedMethods()
                    .filter { it.containingClass != null }
                    .forEach { method ->
                        sourceMethods
                                .getOrPut(method) { SourceMethod(method) }
                    }
        }

        private fun sourceAnnotatedMethods(): Sequence<PsiMethod> = buildSequence {
            if (cTargetClass != null)
                yieldAll(AnnotatedElementsSearch.searchPsiMethods(cTargetClass, scope))
            if (cImplInterface != null)
                yieldAll(AnnotatedElementsSearch.searchPsiMethods(cImplInterface, scope))
        }
    }

    private class SourceMethod(val method: PsiMethod) {
        val aTargetClass: PsiAnnotation?
        val aImplInterface: PsiAnnotation?
        val aNameRegex: PsiAnnotation?
        val aInsert: PsiAnnotation?
        val aProxy: PsiAnnotation?
        val aTryCatchHandler: PsiAnnotation?

        init {
            var tmpTargetClass: PsiAnnotation? = null
            var tmpImplInterface: PsiAnnotation? = null
            var tmpNameRegex: PsiAnnotation? = null
            var tmpInsert: PsiAnnotation? = null
            var tmpProxy: PsiAnnotation? = null
            var tmpTryCatchHandler: PsiAnnotation? = null
            method.annotations.forEach { annotation ->
                when {
                    annotation.fitName(cnTargetClass) -> {
                        tmpTargetClass = annotation
                    }
                    annotation.fitName(cnImplInterface) -> {
                        tmpImplInterface = annotation
                    }
                    annotation.fitName(cnNameRegex) -> {
                        tmpNameRegex = annotation
                    }
                    annotation.fitName(cnInsert) -> {
                        tmpInsert = annotation
                    }
                    annotation.fitName(cnProxy) -> {
                        tmpProxy = annotation
                    }
                    annotation.fitName(cnTryCatchHandler) -> {
                        tmpTryCatchHandler = annotation
                    }
                }
            }
            aTargetClass = tmpTargetClass
            aImplInterface = tmpImplInterface
            aNameRegex = tmpNameRegex
            aInsert = tmpInsert
            aProxy = tmpProxy
            aTryCatchHandler = tmpTryCatchHandler
        }
    }
}

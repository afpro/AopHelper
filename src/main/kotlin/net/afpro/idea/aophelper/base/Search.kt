package net.afpro.idea.aophelper.base

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import kotlin.coroutines.experimental.buildSequence

/**
 * find class by name
 *
 * @param qName qualified name
 * @param project target project
 * @param scope target scope
 * @return all class match name
 */
fun findClassByName(qName: String, project: Project, scope: GlobalSearchScope): Sequence<PsiClass> = buildSequence {
    //    yieldAll(JavaFullClassNameIndex.getInstance().get(qName.hashCode(), project, scope)
//            .asSequence()
//            .filter { it.qualifiedName == qName })
//
//    yieldAll(KotlinFullClassNameIndex.getInstance().get(qName, project, scope)
//            .asSequence()
//            .filter { it.qualifiedClassNameForRendering() == qName }
//            .map { it.toLightClassWithBuiltinMapping() }
//            .filterNotNull())
    TODO("finish this after research")
}

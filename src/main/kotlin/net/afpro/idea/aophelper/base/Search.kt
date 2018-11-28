package net.afpro.idea.aophelper.base

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.asJava.toLightClassWithBuiltinMapping
import org.jetbrains.kotlin.idea.refactoring.memberInfo.qualifiedClassNameForRendering
import org.jetbrains.kotlin.idea.stubindex.KotlinFullClassNameIndex
import kotlin.coroutines.experimental.buildSequence

/**
 * find all class by name
 *
 * @param qName qualified name
 * @param project target project
 * @param scope target scope
 * @return all class match name
 */
fun findAllClassByName(qName: String, project: Project, scope: GlobalSearchScope): Sequence<PsiClass> = buildSequence {
    yieldAll(JavaFullClassNameIndex.getInstance().get(qName.hashCode(), project, scope)
            .asSequence()
            .filter { it.qualifiedName == qName })

    yieldAll(KotlinFullClassNameIndex.getInstance().get(qName, project, scope)
            .asSequence()
            .filter { it.qualifiedClassNameForRendering() == qName }
            .map { it.toLightClassWithBuiltinMapping() }
            .filterNotNull())
}

/**
 * find class by name
 *
 * @param qName qualified name
 * @param project target project
 * @param scope target scope
 * @return all class match name
 */

//就一个？
fun findClassByName(qName: String, project: Project, scope: GlobalSearchScope): PsiClass? =
        findAllClassByName(qName, project, scope).firstOrNull()

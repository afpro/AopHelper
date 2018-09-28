package net.afpro.idea.aophelper.base

import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.DirectClassInheritorsSearch
import org.jetbrains.kotlin.asJava.toLightMethods
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import java.util.*
import javax.swing.Icon
import kotlin.coroutines.experimental.buildSequence

/**
 * find annotation of name {@param name} or null
 *
 * @param name annotation name
 */
fun Array<out PsiAnnotation?>.findAnnotation(name: String): PsiAnnotation? = firstOrNull { it.fitName(name) }

fun PsiAnnotation?.fitName(name: String): Boolean = this?.qualifiedName == name

val PsiElement.tryNameIdentifier: PsiElement
    get() = (this as? PsiNameIdentifierOwner)?.nameIdentifier ?: this

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

fun PsiClass.chainUp(): Sequence<PsiClass> = buildSequence {
    var type = this@chainUp
    while (true) {
        yield(type)
        type = type.superClass ?: break
    }
}

fun PsiClass.canCastTo(targetQName: String, buf: MutableList<PsiClass>? = null): Boolean {
    val tmp = buf ?: LinkedList()
    tmp.clear()
    tmp.push(this)

    while (tmp.isEmpty()) {
        val type = tmp.pop()
        if (type.qualifiedName === targetQName) {
            return true
        }

        tmp.addAll(type.supers)
    }
    return false
}

fun PsiClass.signature(useSlash: Boolean = true): String {
    val file = containingFile
    val qName = qualifiedName ?: ""
    val pkgName = when (file) {
        is PsiJavaFile -> file.packageName
        is KtFile -> file.packageFqName.asString()
        else -> null
    }
    val pkgLen = pkgName?.length ?: 0

    if (qName.length < (pkgName?.length ?: 0)) {
        throw RuntimeException("qName[$qName] shorter than pkgName[$pkgName]")
    }

    val sb = StringBuilder()
    if (pkgLen > 0) {
        if (useSlash) {
            sb.append(pkgName!!.replace('.', '/'))
        } else {
            sb.append(pkgName)
        }
    }

    if (pkgLen > 0) {
        if (useSlash) {
            sb.append('/')
        } else {
            sb.append('.')
        }
        sb.append(qName.substring(pkgLen + 1).replace('.', '$'))
    } else {
        sb.append(qName.replace('.', '$'))
    }

    return sb.toString()
}

fun PsiClass.hasSubClass(): Boolean {
    return DirectClassInheritorsSearch.search(this).any()
}

fun PsiClass.hasSubClass(scope: GlobalSearchScope): Boolean {
    return DirectClassInheritorsSearch.search(this, scope).any()
}

/**
 * try wrap PsiElement into PsiMethod
 */
fun PsiElement.asMethod(): PsiMethod? {
    return when (this) {
        is PsiMethod -> this
        is KtFunction -> {
            val lightMethods = this.toLightMethods()
            if (lightMethods.size == 1) {
                lightMethods[0]
            } else {
                null
            }
        }
        else -> null
    }
}

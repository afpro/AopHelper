package net.afpro.idea.aophelper.lancet

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.search.searches.DirectClassInheritorsSearch
import com.intellij.psi.util.PsiUtil
import net.afpro.idea.aophelper.base.*
import org.jetbrains.kotlin.idea.search.allScope
import java.util.*
import javax.swing.Icon
import kotlin.coroutines.experimental.buildSequence

internal const val cnTargetClass = "me.ele.lancet.base.annotations.TargetClass"
internal const val cnImplInterface = "me.ele.lancet.base.annotations.ImplementedInterface"
internal const val cnNameRegex = "me.ele.lancet.base.annotations.NameRegex"
internal const val cnInsert = "me.ele.lancet.base.annotations.Insert"
internal const val cnProxy = "me.ele.lancet.base.annotations.Proxy"
internal const val cnTryCatchHandler = "me.ele.lancet.base.annotations.TryCatchHandler"
internal const val cnClassOf = "me.ele.lancet.base.annotations.ClassOf"

internal enum class LancetIcon(val icon: Icon) {
    InjectPoint(AllIcons.Graph.ZoomIn),
    TargetPoint(AllIcons.Graph.ZoomOut),
    InvalidInjectPoint(AllIcons.General.Error),
}

internal enum class LancetScope {
    SELF,
    DIRECT,
    ALL,
    LEAF;
}



private fun PsiAnnotationMemberValue?.asLancetScope(): LancetScope {
    val text = this?.text ?: return LancetScope.SELF
    val lastDot = text.lastIndexOf('.')
    val name = if (lastDot < 0) {
        text
    } else {
        text.substring(lastDot + 1, text.length)
    }
    return LancetScope.valueOf(name)
}

private fun PsiAnnotationMemberValue?.asText(): String {
    //text是指
    val text = this?.text ?: return ""
    return text.substring(1, text.length - 1)
}

internal data class TargetClassInfo(
        val value: String,
        val scope: LancetScope) {
    fun match(type: PsiClass): Boolean {
        if (type.isInterface) {
            return false
        }

        if (scope == LancetScope.LEAF && type.hasSubClass()) {
            return false
        }

        return when (scope) {
            LancetScope.SELF -> {
                value == type.qualifiedName
            }
            LancetScope.DIRECT -> {
                value == type.qualifiedName
                        || value == type.superClass?.qualifiedName
            }
            LancetScope.ALL, LancetScope.LEAF -> {
                type.chainUp().any {
                    value == it.qualifiedName
                }
            }
        }
    }

    fun possibleTargetClass(searchProject: Project, searchScope: GlobalSearchScope): Sequence<PsiClass> = buildSequence {
        val targetType = findClassByName(value, searchProject, searchScope) ?: return@buildSequence
        if (targetType.isInterface) {
            return@buildSequence
        }

        when (scope) {
            LancetScope.SELF -> {
                yield(targetType)
            }
            LancetScope.DIRECT -> {
                yield(targetType)
                DirectClassInheritorsSearch.search(targetType, searchScope).forEach { subType ->
                    yield(subType)
                }
            }
            LancetScope.ALL -> {
                yield(targetType)
                yieldAll(ClassInheritorsSearch.search(targetType))
            }
            LancetScope.LEAF -> {
                val types = ClassInheritorsSearch.search(targetType).toMutableSet()
                types.add(targetType)
                yieldAll(types.asSequence().filterNot { types.contains(it.superClass) })
            }
        }
    }

    companion object {
        fun get(annotations: Array<out PsiAnnotation>): TargetClassInfo? {
            return annotations.findAnnotation(cnTargetClass)?.let {
                TargetClassInfo(
                        it.findAttributeValue("value").asText(),
                        it.findAttributeValue("scope").asLancetScope())
            }
        }
    }
}


internal data class ImplInterfaceInfo(
        val value: Set<String>,
        val scope: LancetScope) {
    fun match(type: PsiClass): Boolean {
        if (type.isInterface) {
            return false
        }

        if (scope == LancetScope.LEAF && type.hasSubClass()) {
            return false
        }

        return when (scope) {
            LancetScope.SELF -> {
                type.interfaces.any {
                    it.qualifiedName in value
                }
            }
            LancetScope.DIRECT -> {
                val tmp = LinkedList<PsiClass>()
                tmp.add(type)
                while (!tmp.isEmpty()) {
                    val interfaceType = tmp.removeFirst()
                    interfaceType.interfaces.forEach { tmp.addLast(it) }
                    if (interfaceType.qualifiedName in value) {
                        return true
                    }
                }
                false
            }
            LancetScope.ALL, LancetScope.LEAF -> {
                val tmp = LinkedList<PsiClass>()
                tmp.add(type)
                while (!tmp.isEmpty()) {
                    val t = tmp.removeFirst()
                    if (t.isInterface) {
                        if (t.qualifiedName in value) {
                            return true
                        }
                    } else {
                        tmp.addLast(t.superClass)
                    }
                    t.interfaces.forEach { tmp.add(it) }
                }
                false
            }
        }
    }

    fun possibleTargetClass(searchProject: Project, searchScope: GlobalSearchScope): Sequence<PsiClass> {
        return value.asSequence()
                .flatMap { possibleTargetClass(it, searchProject, searchScope) }
    }

    private fun possibleTargetClass(value: String, searchProject: Project, searchScope: GlobalSearchScope): Sequence<PsiClass> = buildSequence {
        val targetInterface = findClassByName(value, searchProject, searchScope) ?: return@buildSequence
        if (!targetInterface.isInterface) {
            return@buildSequence
        }

        when (scope) {
            LancetScope.SELF -> {
                if (targetInterface.isInterface) {
                    yieldAll(DirectClassInheritorsSearch.search(targetInterface))
                }
            }
            LancetScope.DIRECT -> {
                yieldAll(ClassInheritorsSearch.search(targetInterface).asSequence()
                        .filterNot { it.isInterface }
                        .filter { targetInterface in it.interfaces })
            }
            LancetScope.ALL -> {
                yieldAll(ClassInheritorsSearch.search(targetInterface))
            }
            LancetScope.LEAF -> {
                val types = ClassInheritorsSearch.search(targetInterface).toMutableSet()
                types.add(targetInterface)
                yieldAll(types.asSequence().filterNot { types.contains(it.superClass) })
            }
        }
    }

    companion object {
        fun get(annotations: Array<out PsiAnnotation>): ImplInterfaceInfo? {
            return annotations.findAnnotation(cnImplInterface)?.let {
                ImplInterfaceInfo(
                        values(it.findAttributeValue("value")),
                        it.findAttributeValue("scope").asLancetScope())
            }
        }

        private fun values(annotationValue: PsiAnnotationMemberValue?): Set<String> {
            return when (annotationValue) {
                is PsiArrayInitializerMemberValue -> {
                    annotationValue.initializers.map { it.asText() }.toSet()
                }
                else -> {
                    return setOf(annotationValue.asText())
                }
            }
        }
    }
}

internal data class InsertInfo(
        val value: String,
        val mayCreateSuper: Boolean) {
    companion object {
        fun get(annotations: Array<out PsiAnnotation>): InsertInfo? {
            return annotations.findAnnotation(cnInsert)?.let {
                InsertInfo(
                        it.findAttributeValue("value").asText(),
                        it.findAttributeValue("mayCreateSuper")?.text?.equals("true", ignoreCase = true) ?: false)
            }
        }
    }
}

internal data class ProxyInfo(
        val value: String) {
    companion object {
        fun get(annotations: Array<out PsiAnnotation>): ProxyInfo? {
            return annotations.findAnnotation(cnProxy)?.let {
                ProxyInfo(it.findAttributeValue("value").asText())
            }
        }
    }
}

internal data class NameRegexInfo(
        val value: String) {
    val regex by lazy(LazyThreadSafetyMode.NONE) {
        Regex(value)
    }

    companion object {
        fun get(annotations: Array<out PsiAnnotation>): NameRegexInfo? {
            return annotations.findAnnotation(cnNameRegex)?.let {
                NameRegexInfo(it.findAttributeValue("value").asText())
            }
        }
    }
}

internal data class LancetInfo(
        val injectMethod: PsiMethod,
        val targetClassInfo: TargetClassInfo?,
        val implInterfaceInfo: ImplInterfaceInfo?,
        val proxyInfo: ProxyInfo?,
        val insertInfo: InsertInfo?,
        val nameRegexInfo: NameRegexInfo?,
        val isTryCatchHandler: Boolean) {
    val retType by lazy(LazyThreadSafetyMode.NONE) {
        val classOf = injectMethod.annotations.findAnnotation(cnClassOf)
        classOf?.findAttributeValue("value")?.asText()
                ?: lancetClassOfName(injectMethod.returnType)
    }

    val paramTypes by lazy(LazyThreadSafetyMode.NONE) {
        injectMethod.parameterList.parameters.asSequence()
                .map {
                    val classOf = it.annotations.findAnnotation(cnClassOf)
                    classOf?.findAttributeValue("value")?.asText()
                            ?: lancetClassOfName(it.type)
                }
                .toList()
    }

    /**
     * check if this injectMethod is target of this lancet injection point
     */
    fun match(method: PsiMethod): Boolean {
        return matchMethodName(method)
                && matchMethodSig(method)
                && matchClassName(method.containingClass)
                && matchNameRegex(method.containingClass)
    }

    fun match(clazz: PsiClass): Boolean {
        if (!matchClassName(clazz) || !matchNameRegex(clazz)) {
            return false
        }

        val hasMatchedMethod = clazz.children.asSequence()
                .map { it.asMethod() }
                .filterNotNull()
                .filter(this::matchMethodName)
                .filter(this::matchMethodSig)
                .any()
        if (hasMatchedMethod) {
            return false
        }

        return hasMethodToOverride(clazz)
    }

    fun matchMethodName(method: PsiMethod): Boolean {
        if (proxyInfo != null
                && proxyInfo.value != method.name) {
            return false
        }

        if (insertInfo != null
                && insertInfo.value != method.name) {
            return false
        }

        return true
    }

    fun matchMethodSig(method: PsiMethod): Boolean {
        val injectModList = method.modifierList
        val targetModList = method.modifierList
        mustSameModifiers.forEach {
            if (injectModList.hasModifierProperty(it) != targetModList.hasModifierProperty(it)) {
                return false
            }
        }

        if (retType != lancetClassOfName(method.returnType)) {
            return false
        }

        if (injectMethod.parameterList.parametersCount != method.parameterList.parametersCount) {
            return false
        }

        val injectParamTypes = paramTypes
        val targetParamTypes = method.parameterList.parameters
        (injectParamTypes zip targetParamTypes).forEach {
            if (it.first != lancetClassOfName(it.second.type)) {
                return false
            }
        }

        return true
    }

    fun matchClassName(type: PsiClass?): Boolean {
        type ?: return false

        if (targetClassInfo != null && !targetClassInfo.match(type)) {
            return false
        }

        if (implInterfaceInfo != null && !implInterfaceInfo.match(type)) {
            return false
        }

        return true
    }

    fun matchNameRegex(type: PsiClass?): Boolean {
        if (nameRegexInfo == null) {
            return true
        }

        val classSig = type?.signature() ?: return false
        return nameRegexInfo.regex.matches(classSig)
    }

    /**
     * find all targets of this lancet injection point
     */
    fun findAllTargets(): Sequence<PsiElement> {

        return possibleTargetClass()
                .filterNot(PsiClass::isInterface)
                .filter(this::matchNameRegex)
                .flatMap {
                    buildSequence {
                        // declared methods
                        var foundDeclMethod = false
                        it.children.asSequence()
                                .forEach { el ->
                                    val asMethod = el.asMethod() ?: return@forEach
                                    if (matchMethodName(asMethod) && matchMethodSig(asMethod)) {
                                        yield(el)
                                        foundDeclMethod = true
                                    }
                                }

                        if (!foundDeclMethod && hasMethodToOverride(it)) {
                            yield(it)
                        }
                    }
                }
    }

    private fun possibleTargetClass(): Sequence<PsiClass> = buildSequence {
        val proj = PsiUtil.getProjectInReadAction(injectMethod)
        val scope = proj.allScope()

        if (targetClassInfo != null) {
            yieldAll(targetClassInfo.possibleTargetClass(proj, scope))
        }

        if (implInterfaceInfo != null) {
            yieldAll(implInterfaceInfo.possibleTargetClass(proj, scope))
        }
    }

    private fun hasMethodToOverride(clazz: PsiClass): Boolean {
        return clazz.chainUp()
                .flatMap { it.children.asSequence() }
                .map { it.asMethod() }
                .filterNotNull()
                .filter(this::matchMethodName)
                .filter {
                    !it.hasModifierProperty(PsiModifier.PRIVATE)
                            && !it.hasModifierProperty(PsiModifier.STATIC)
                            && !it.hasModifierProperty(PsiModifier.FINAL)
                }
                .filter(this::matchMethodSig)
                .any()
    }

    companion object {
        private val mustSameModifiers = arrayOf(
                PsiModifier.PUBLIC,
                PsiModifier.PROTECTED,
                PsiModifier.PRIVATE,
                PsiModifier.PACKAGE_LOCAL,
                PsiModifier.STATIC)

        fun get(method: PsiMethod, check: Boolean = true): LancetInfo? {
            val annotations = method.annotations
            if (annotations.isEmpty()) {
                return null
            }

            val targetClassInfo = TargetClassInfo.get(annotations)
            val implInterfaceInfo = ImplInterfaceInfo.get(annotations)
            if (check && (targetClassInfo != null).asInt + (implInterfaceInfo != null).asInt != 1) {
                // must have exactly one of them
                return null
            }

            val proxyInfo = ProxyInfo.get(annotations)
            val insertInfo = InsertInfo.get(annotations)
            val hasTryCatchHandler = annotations.findAnnotation(cnTryCatchHandler) != null
            if (check && (proxyInfo != null).asInt + (insertInfo != null).asInt + hasTryCatchHandler.asInt != 1) {
                // must have exactly one of them
                return null
            }

            val nameRegexInfo = NameRegexInfo.get(annotations)
            if (check && nameRegexInfo != null && proxyInfo == null) {
                // NameRegex must be used with Proxy
                return null
            }

            return LancetInfo(
                    method,
                    targetClassInfo,
                    implInterfaceInfo,
                    proxyInfo,
                    insertInfo,
                    nameRegexInfo,
                    hasTryCatchHandler)
        }

        fun lancetClassOfName(type: PsiType?): String {
            type ?: return ""
            return when (type) {
                is PsiPrimitiveType -> type.binaryName()
                is PsiArrayType -> "${lancetClassOfName(type.componentType)}[]"
                is PsiClassType -> type.resolve()?.signature(useSlash = false) ?: ""
                else -> type.canonicalText
            }
        }
    }


}

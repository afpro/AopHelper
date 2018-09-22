package net.afpro.idea.aophelper.lancet

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiAnnotationMemberValue
import com.intellij.psi.PsiMethod
import com.intellij.util.ui.ColorIcon
import net.afpro.idea.aophelper.base.findAnnotation
import java.awt.Color
import javax.swing.Icon

internal const val cnTargetClass = "me.ele.lancet.base.annotations.TargetClass"
internal const val cnImplInterface = "me.ele.lancet.base.annotations.ImplementedInterface"
internal const val cnNameRegex = "me.ele.lancet.base.annotations.NameRegex"
internal const val cnInsert = "me.ele.lancet.base.annotations.Insert"
internal const val cnProxy = "me.ele.lancet.base.annotations.Proxy"
internal const val cnTryCatchHandler = "me.ele.lancet.base.annotations.TryCatchHandler"

internal enum class LancetIcon(val icon: Icon) {
    InjectPoint(ColorIcon(16, Color(0x33, 0x66, 0x99))),
    TargetPoint(ColorIcon(16, Color(0x33, 0x66, 0x99))),
    InvalidInjectPoint(ColorIcon(16, Color(0x33, 0x66, 0x99))),
}

internal enum class LancetScope {
    SELF,
    DIRECT,
    ALL,
    LEAF;

    companion object {
        internal const val prefix = "me.ele.lancet.base.Scope."
    }
}

private fun PsiAnnotationMemberValue.asLancetScope(): LancetScope {
    val text = text
    if (!text.startsWith(LancetScope.prefix)) {
        throw RuntimeException("invalid lancet scope $text")
    }
    return LancetScope.valueOf(text.substring(LancetScope.prefix.length))
}

private fun PsiAnnotationMemberValue.asText(): String {
    val text = text
    return text.substring(1, text.length - 1)
}

internal data class TargetClassInfo(
        val value: String,
        val scope: LancetScope) {
    companion object {
        fun get(annotations: Array<out PsiAnnotation>): TargetClassInfo? {
            return annotations.findAnnotation(cnTargetClass)?.let {
                TargetClassInfo(
                        it.findAttributeValue("value")!!.asText(),
                        it.findAttributeValue("scope")!!.asLancetScope())
            }
        }
    }
}

internal data class ImplInterfaceInfo(
        val value: String,
        val scope: LancetScope) {
    companion object {
        fun get(annotations: Array<out PsiAnnotation>): ImplInterfaceInfo? {
            return annotations.findAnnotation(cnImplInterface)?.let {
                ImplInterfaceInfo(
                        it.findAttributeValue("value")!!.asText(),
                        it.findAttributeValue("scope")!!.asLancetScope())
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
                        it.findAttributeValue("value")!!.asText(),
                        it.findAttributeValue("mayCreateSuper")!!.text.equals("true", ignoreCase = true))
            }
        }
    }
}

internal data class ProxyInfo(
        val value: String) {
    companion object {
        fun get(annotations: Array<out PsiAnnotation>): ProxyInfo? {
            return annotations.findAnnotation(cnProxy)?.let {
                ProxyInfo(it.findAttributeValue("value")!!.asText())
            }
        }
    }
}

internal data class NameRegexInfo(
        val value: String) {
    companion object {
        fun get(annotations: Array<out PsiAnnotation>): NameRegexInfo? {
            return annotations.findAnnotation(cnNameRegex)?.let {
                NameRegexInfo(it.findAttributeValue("value")!!.asText())
            }
        }
    }
}

internal data class LancetInfo(
        val method: PsiMethod,
        val targetClassInfo: TargetClassInfo?,
        val implInterfaceInfo: ImplInterfaceInfo?,
        val proxyInfo: ProxyInfo?,
        val insertInfo: InsertInfo?,
        val nameRegexInfo: NameRegexInfo?,
        val isTryCatchHandler: Boolean) {
    /**
     * check if this method is target of this lancet injection point
     */
    fun match(method: PsiMethod): Boolean {
        TODO("finish this")
    }

    /**
     * find all targets of this lancet injection point
     */
    fun findAllTargets(): Sequence<PsiMethod> {
        TODO("finish this")
    }

    companion object {
        fun get(method: PsiMethod): LancetInfo? {
            val annotations = method.annotations
            if (annotations.isEmpty()) {
                return null
            }

            val targetClassInfo = TargetClassInfo.get(annotations)
            val implInterfaceInfo = ImplInterfaceInfo.get(annotations)
            if (targetClassInfo == null && implInterfaceInfo == null) {
                return null
            }

            return LancetInfo(
                    method,
                    targetClassInfo,
                    implInterfaceInfo,
                    ProxyInfo.get(annotations),
                    InsertInfo.get(annotations),
                    NameRegexInfo.get(annotations),
                    annotations.findAnnotation(cnTryCatchHandler) != null)
        }
    }
}

package net.afpro.idea.aophelper.lancet

import com.intellij.util.ui.ColorIcon
import java.awt.Color
import javax.swing.Icon

internal const val cnTargetClass = "me.ele.lancet.base.annotations.TargetClass"
internal const val cnImplInterface = "me.ele.lancet.base.annotations.ImplementedInterface"
internal const val cnNameRegex = "me.ele.lancet.base.annotations.NameRegex"
internal const val cnInsert = "me.ele.lancet.base.annotations.Insert"
internal const val cnProxy = "me.ele.lancet.base.annotations.Proxy"
internal const val cnTryCatchHandler = "me.ele.lancet.base.annotations.TryCatchHandler"

internal val sourceIconWithTarget by lazy(LazyThreadSafetyMode.NONE) {
    ColorIcon(16, Color(0x33, 0x66, 0x99))
}

internal val sourceIconWithOutTarget by lazy(LazyThreadSafetyMode.NONE) {
    ColorIcon(16, Color(0x33, 0x66, 0x99))
}

internal val targetIcon by lazy(LazyThreadSafetyMode.NONE) {
    ColorIcon(16, Color(0x33, 0x66, 0x99))
}

internal fun getMarkIcon(isSource: Boolean, hasTargets: Boolean = false): Icon {
    return if (isSource) {
        if (hasTargets) {
            sourceIconWithTarget
        } else {
            sourceIconWithOutTarget
        }
    } else {
        targetIcon
    }
}


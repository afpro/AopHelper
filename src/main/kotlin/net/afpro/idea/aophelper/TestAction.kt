package net.afpro.idea.aophelper

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import net.afpro.idea.aophelper.base.findClassByName
import org.jetbrains.kotlin.idea.search.allScope

class TestAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val proj = event.dataContext.getData(DataKeys.PROJECT) ?: return
        val scope = proj.allScope()

        val cStr = findClassByName("java.lang.String", proj, scope)
        val cSeq = findClassByName("kotlin.sequences.Sequence", proj, scope)

        Notifications.Bus.notify(Notification("AopHelper", "Test", "str: $cStr", NotificationType.INFORMATION))
        Notifications.Bus.notify(Notification("AopHelper", "Test", "seq: $cSeq", NotificationType.INFORMATION))
    }

}
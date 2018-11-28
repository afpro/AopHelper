package net.afpro.idea.aophelper

import com.intellij.ide.util.EditSourceUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import net.afpro.idea.aophelper.lancet.Cache
import net.afpro.idea.aophelper.lancet.LancetLineMarkerProvider
import org.jetbrains.kotlin.idea.search.allScope
import org.jetbrains.kotlin.j2k.getContainingClass
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.WindowConstants


class LancetListDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent?) {
        val jf = JFrame("All Lancet Class List")
        jf.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        jf.setLocationRelativeTo(null)
        jf.setSize(500, 500)


        val jList = JList<String>()
        jList.preferredSize = Dimension(200, 100)
//        jList.foreground = Color.GREEN


        val proj = e?.dataContext?.getData(DataKeys.PROJECT) ?: return
        val scope = proj.allScope()

        val lancetTypes = LancetLineMarkerProvider.LancetTypes(proj)
        val names = lancetTypes.possibleInjectPoints.map {
            "Class Name:" + it.key.getContainingClass()?.qualifiedName + "    Method Name:" + it.key.text
        }.toList()

        val methodList = lancetTypes.possibleInjectPoints.map {
            it.key
        }.toList()


        /* val names = Cache.allMatchPsiElement.map {
             "Class Name:" + it.getContainingClass()?.qualifiedName + "    Method Name:" + it.text
         }.toList()*/


        jList.setListData(toArray<String>(names))

        jList.addListSelectionListener {

            val indices = jList.selectedIndices
            val jListModel = jList.model


            Notifications.Bus.notify(Notification("AopHelper", "Test", "index:" + indices.size + "0" + Cache.allMatchPsiElement.size, NotificationType.INFORMATION))


            indices.forEach {
                gotoTargetElement(methodList[it])

            }

        }
        val panel = JPanel()

        panel.add(jList)

        jf.contentPane = jList
        jf.isVisible = true
    }


    private fun gotoTargetElement(element: PsiElement): Boolean {
        val navigatable = if (element is Navigatable) element else EditSourceUtil.getDescriptor(element)
        if (navigatable != null && navigatable.canNavigate()) {
            navigatable.navigate(true)
            return true
        } else {
            return false
        }
    }

    inline fun <reified T> toArray(list: List<*>): Array<T> {
        return (list as List<T>).toTypedArray()
    }

}

package net.afpro.idea.aophelper

import com.intellij.ide.util.EditSourceUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import net.afpro.idea.aophelper.lancet.LancetElement
import net.afpro.idea.aophelper.lancet.LancetLineMarkerProvider
import net.afpro.idea.aophelper.lancet.LancetTabModel
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer


class LancetListDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent?) {


        val frame = JFrame("Lancet插桩列表")

        val scrollPane = JScrollPane()
        val columnNames = listOf("Lancet Class", "Target Class", "Target Method")


        val project = e?.dataContext?.getData(DataKeys.PROJECT) ?: return
        val lancetTypes = LancetLineMarkerProvider.LancetTypes(project)
        val rowData = lancetTypes.possibleInjectPoints.map {
            LancetElement(it.key.containingClass?.name, it.value.targetClassInfo?.value, it.key.name)
        }.toList()


        val tableModel = LancetTabModel(columnNames, rowData)
        val table = JTable(tableModel)

        val tableCellRenderer = DefaultTableCellRenderer()

        tableCellRenderer.horizontalAlignment = JLabel.CENTER
        table.setDefaultRenderer(Any::class.java, tableCellRenderer)

        scrollPane.setViewportView(table)
        frame.contentPane.add(scrollPane, BorderLayout.CENTER)

        frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        frame.setBounds(300, 200, 800, 400)



        table.setShowGrid(false)



        table.rowHeight = 40


        val selectionModel = table.selectionModel
        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        selectionModel.addListSelectionListener {
            val selectedRow = table.selectedRow
            val psi = lancetTypes.possibleInjectPoints.map {
                it.key
            }.toList()[selectedRow]

            gotoTargetElement(psi)
        }

        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true


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

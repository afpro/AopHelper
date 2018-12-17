package net.afpro.idea.aophelper

import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.DialogWrapper
import java.awt.datatransfer.StringSelection
import javax.swing.JCheckBox

class LancetGenerateDialogDelegate(val dialog: LancetGenerateDialog) {
    private val insertUI = mapOf(
            dialog.targetClassTextField to dialog.targetClassCheckBox,
            dialog.insertTextField to dialog.insertCheckBox
    )


    init {
        dialog.apply {
            setTitle("Generate Lancet Code")
            addOkAction().setText("Copy")
            setOkOperation {
                val result=dialog.generateCodeTextField!!.text
                CopyPasteManager.getInstance().setContents(StringSelection(result))
                dialogWrapper.close(DialogWrapper.OK_EXIT_CODE, false)
            }
            addCancelAction()


            insertUI.entries.forEach {

            }
        }
    }

}
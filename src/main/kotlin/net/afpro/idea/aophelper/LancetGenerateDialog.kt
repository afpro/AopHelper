package net.afpro.idea.aophelper

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField

class LancetGenerateDialog : DialogBuilder {

   internal var contentPane: JPanel? = null

    var targetClassTextField: JTextField? = null
    var targetClassCheckBox: JTextArea? = null

    var insertTextField: JTextField? = null
    var insertCheckBox: JTextArea? = null


    var generateCodeTextField: JTextArea? = null

    var lancetGenerateDialogDelegate: LancetGenerateDialogDelegate? = null

    constructor(project: Project?) : super(project) {
        lancetGenerateDialogDelegate = LancetGenerateDialogDelegate(this)
        setCenterPanel(contentPane)
    }
}
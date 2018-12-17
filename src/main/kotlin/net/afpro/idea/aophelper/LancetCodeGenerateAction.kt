package net.afpro.idea.aophelper

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.DocumentAdapter
import net.afpro.idea.aophelper.base.asMethod
import org.jetbrains.kotlin.asJava.toLightMethods
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.resolve.jvm.KotlinJavaPsiFacade
import java.awt.Font
import java.util.*
import javax.swing.event.DocumentEvent


class LancetCodeGenerateAction : AnAction() {


    override fun actionPerformed(e: AnActionEvent?) {

        val project = e?.getData(CommonDataKeys.PROJECT) ?: return
        val dataContext = e.dataContext


        val editor = Objects.requireNonNull<Editor>(CommonDataKeys.EDITOR.getData(dataContext))
        val file = Objects.requireNonNull<PsiFile>(CommonDataKeys.PSI_FILE.getData(dataContext))
        try {
            isKotlin = e.getData(CommonDataKeys.PSI_FILE) is KtFile
        } catch (e: NoClassDefFoundError) {
            isKotlin = false
        }


        val offset = editor.caretModel.offset
        val psiElement = file!!.findElementAt(offset) ?: return


        var psiMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod::class.java)

        if (isKotlin) {
            val ktMethod = PsiTreeUtil.getParentOfType(psiElement, KtFunction::class.java)
            val lightMethods = ktMethod?.toLightMethods()

            if (lightMethods != null && lightMethods.size == 1) {
                psiMethod = lightMethods[0]
            }

        }
        if (psiMethod == null) {
            Notifications.Bus.notify(Notification("AopHelper", "Focus On  Method", "You should focus on a method to use Lancet", NotificationType.INFORMATION))
            return
        }

        val dialog = initDialog(project, psiMethod)
        dialog.showAndGet()
    }
}

var isKotlin: Boolean = false

private fun initDialog(project: Project, psiMethod: PsiMethod): LancetGenerateDialog {
    val className = psiMethod.containingClass?.qualifiedName
    val methodName = psiMethod.name
    val dialog = LancetGenerateDialog(project)
    dialog.targetClassTextField!!.text = className
    dialog.insertTextField!!.text = methodName
    dialog.generateCodeTextField!!.isEditable = true
    dialog.generateCodeTextField!!.font = Font(null, Font.PLAIN, 15)
    val docListener = object : DocumentAdapter() {
        override fun textChanged(e: DocumentEvent) {
            generateCode(dialog, psiMethod)
        }
    }
    generateCode(dialog, psiMethod)
    dialog.targetClassTextField!!.document.addDocumentListener(docListener)
    dialog.insertTextField!!.document.addDocumentListener(docListener)

    return dialog
}


private fun generateCode(dialog: LancetGenerateDialog, psiMethod: PsiMethod): StringBuilder {


    val codeBlock = JavaPsiFacade.getInstance(psiMethod.project).elementFactory.createCodeBlock()
    val code = StringBuilder()
    val copyMethod = psiMethod.copy()
    if (!isKotlin && copyMethod is PsiMethod && psiMethod.body != null) {
        copyMethod.body!!.replace(codeBlock)
    } else if (copyMethod is PsiMethod && psiMethod.body != null) {
        val firstIndex = copyMethod.text.indexOfFirst { it == '{' }
        val lastIndex = copyMethod.text.lastIndexOf("{")
    }

    code.append("@TargetClass" + "(\"" + dialog.targetClassTextField!!.text + "\")\n")
    code.append("@Insert" + "(\"" + dialog.insertTextField!!.text + "\")\n")
    code.append(copyMethod.text)
    dialog.generateCodeTextField!!.text = code.toString()
    return code
}


@Deprecated("Useless")
private fun generateMethodSignature(psiMethod: PsiMethod) {
    val emptyBlank = " "
    val returnType = psiMethod.returnType?.getCanonicalText(true)
    val lastDot = returnType?.lastIndexOf('.')
    var returnText = returnType
    if (lastDot != null) {
        if (lastDot > 0) {
            returnText = returnType.substring(lastDot + 1, returnType.length)
        }
    }
    var accessModifier = PsiModifier.PUBLIC
    if (psiMethod.hasModifierProperty(PsiModifier.PUBLIC)) {
        accessModifier = PsiModifier.PUBLIC
    } else if (psiMethod.hasModifierProperty(PsiModifier.PRIVATE)) {
        accessModifier = PsiModifier.PRIVATE
    } else if (psiMethod.hasModifierProperty(PsiModifier.PROTECTED)) {
        accessModifier = PsiModifier.PROTECTED
    }
    if (psiMethod.hasModifierProperty(PsiModifier.SYNCHRONIZED)) {
        accessModifier += emptyBlank + PsiModifier.SYNCHRONIZED
    }
    if (psiMethod.hasModifierProperty(PsiModifier.ABSTRACT)) {
        accessModifier += emptyBlank + PsiModifier.ABSTRACT
    }
    if (psiMethod.hasModifierProperty(PsiModifier.STATIC)) {
        accessModifier += emptyBlank + PsiModifier.STATIC
    }
    if (psiMethod.hasModifierProperty(PsiModifier.FINAL)) {
        accessModifier += emptyBlank + PsiModifier.FINAL
    }
    val parameterList = psiMethod.parameterList
}

@Deprecated("Useless")
private fun generateCode(dialog: LancetGenerateDialog, accessModifier: String, returnType: String?, methodName: String?, parameterList: PsiParameterList): StringBuilder {
    val code = StringBuilder()
    val emptyBlank = " "
    code.append("@TargetClass" + "(\"" + dialog.targetClassTextField!!.text + "\")\n")
    code.append("@Insert" + "(\"" + dialog.insertTextField!!.text + "\")\n")
    code.append(accessModifier + emptyBlank)
    code.append(returnType + emptyBlank)
    code.append(methodName)
    code.append(parameterList.text + "{\n")
    code.append("\n")
    code.append("}\n")
    dialog.generateCodeTextField!!.text = code.toString()
    return code
}



package world.gregs.intellij.plugins.flow

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter

class TestAction : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null && e.getData(CommonDataKeys.NAVIGATABLE) is PsiParameter
    }

    override fun actionPerformed(e: AnActionEvent) {
        // Using the event, create and show a dialog
        val message = StringBuffer(e.presentation.text + " Selected!").appendln()
        val dlgTitle = e.presentation.description
        // If an element is selected in the editor, add info about it.
        val nav = e.getData(CommonDataKeys.NAVIGATABLE)
        if (nav != null && nav is PsiParameter) {
            if(nav.isVarArgs) {
                Messages.showInfoMessage("Can't be applied to variable arguments", "Nope")
                return
            }
            message.append("Selected Element: $nav").appendln()

            val declaration = nav.declarationScope
            message.append("Scope: $declaration").appendln()

            if(declaration is PsiMethod) {
                declaration.findSuperMethodSignaturesIncludingStatic(true).forEach {
                    message.append("Sig: $it").appendln()
                }
                declaration.findSuperMethods().forEach {
                    message.append("Super methods: $it").appendln()
                }
                declaration.findDeepestSuperMethods().forEach {
                    message.append("Deep super methods: $it").appendln()
                }
            }
        }

        println("Response: " + Messages.showYesNoCancelDialog(message.toString(), dlgTitle, Messages.getWarningIcon()))
    }

}
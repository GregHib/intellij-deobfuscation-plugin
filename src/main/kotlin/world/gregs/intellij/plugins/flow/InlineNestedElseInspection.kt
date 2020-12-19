package world.gregs.intellij.plugins.flow

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilCore
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import com.siyeh.ig.psiutils.CommentTracker
import com.siyeh.ig.style.ControlFlowStatementVisitorBase
import com.siyeh.ig.style.SingleStatementInBlockInspection
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NotNull
import world.gregs.intellij.plugins.DeobfuscateToolBundle

class InlineNestedElseInspection : SingleStatementInBlockInspection() {

    override fun getDisplayName(): String {
        return DeobfuscateToolBundle.message("inline.nested.else.display.name")
    }

    public override fun buildErrorString(vararg infos: Any): String {
        return DeobfuscateToolBundle.message("inline.nested.else.problem.descriptor")
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : ControlFlowStatementVisitorBase() {
            override fun isApplicable(body: PsiStatement?): Boolean {
                //If body is else branch of parent
                val parent = body?.parent
                if (body is PsiBlockStatement && parent is PsiIfStatement && parent.elseBranch == body) {
                    val codeBlock = body.codeBlock
                    if (PsiUtilCore.hasErrorElementChild(codeBlock)) {
                        return false
                    }

                    //Else block must only contain a single if statement
                    val statement = codeBlock.statements.firstOrNull()
                    if (codeBlock.statementCount == 1 && statement is PsiIfStatement) {
                        if (PsiUtilCore.hasErrorElementChild(statement)) {
                            return false
                        }
                        return true
                    }
                }
                return false
            }

            override fun getOmittedBodyBounds(body: PsiStatement?): Pair<PsiElement, PsiElement>? {
                if (body is PsiBlockStatement) {
                    val codeBlock = body.codeBlock
                    if (codeBlock.statementCount == 1) {
                        val statement = codeBlock.statements.firstOrNull()
                        if (statement?.textContains('\n') == true) {
                            return Pair(statement, statement)
                        }
                    }
                }
                return null
            }

        }
    }

    override fun buildFix(vararg infos: Any?): InspectionGadgetsFix {
        return object : InspectionGadgetsFix() {
            @Nls
            @NotNull
            override fun getName(): String {
                return DeobfuscateToolBundle.message("inline.nested.else.quickfix")
            }

            @Nls
            @NotNull
            override fun getFamilyName(): String {
                return DeobfuscateToolBundle.message("inline.nested.else.family.quickfix")
            }

            override fun doFix(project: Project, descriptor: ProblemDescriptor) {
                var statement = PsiTreeUtil.getNonStrictParentOfType(descriptor.startElement, PsiStatement::class.java)
                        ?: return

                if (statement is PsiBlockStatement) {
                    statement = PsiTreeUtil.getNonStrictParentOfType(statement.parent, PsiStatement::class.java)
                            ?: return
                }

                val body = when (statement) {
                    is PsiLoopStatement -> statement.body
                    is PsiIfStatement -> statement.elseBranch
                    else -> null
                } as? PsiBlockStatement ?: return

                val child = body.codeBlock.statements.firstOrNull()
                if (body.codeBlock.statementCount != 1 || child == null) {
                    return
                }

                val ct = CommentTracker()
                val text = ct.text(child)
                val replacementExp = ct.replace(body, text)
                CodeStyleManager.getInstance(project).reformat(replacementExp)
                ct.insertCommentsBefore(statement)
            }
        }
    }
}
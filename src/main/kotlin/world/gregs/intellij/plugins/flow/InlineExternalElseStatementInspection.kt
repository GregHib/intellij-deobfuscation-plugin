package world.gregs.intellij.plugins.flow

import com.intellij.codeInsight.intention.impl.InvertIfConditionAction
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.psi.*
import com.intellij.psi.JavaTokenType.NE
import com.intellij.psi.PsiKeyword.FALSE
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import com.siyeh.ig.psiutils.CommentTracker
import com.siyeh.ig.style.ControlFlowStatementVisitorBase
import com.siyeh.ig.style.SingleStatementInBlockInspection
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NotNull
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import kotlin.math.exp

/**
 * TODO what does this do? find a more descriptive name.
 */
class InlineExternalElseStatementInspection : SingleStatementInBlockInspection() {

    override fun getDisplayName(): String {
        return DeobfuscateToolBundle.message("inline.external.else.statement.display.name")
    }

    public override fun buildErrorString(vararg infos: Any): String {
        return DeobfuscateToolBundle.message("inline.external.else.statement.problem.descriptor")
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : ControlFlowStatementVisitorBase() {

            override fun visitIfStatement(statement: PsiIfStatement) {
                super.visitIfStatement(statement)
                val expression = statement.condition as? PsiBinaryExpression ?: return//TODO support other conditional expressions
                if(expression.operationSign.text == "!=" && statement.elseBranch == null && statement.elseElement == null) {
                    val parent = statement.parent as? PsiStatement ?: return
                    val parents = parent.parent.children
                    if(parents[parents.size - 3] is PsiReturnStatement) {
                        registerStatementError(statement)
                    }
                }
            }

            override fun isApplicable(body: PsiStatement?): Boolean {
                return false
            }

            override fun getOmittedBodyBounds(body: PsiStatement?): Pair<PsiElement, PsiElement>? {
                return null
            }

        }
    }

    override fun buildFix(vararg infos: Any?): InspectionGadgetsFix? {
        return object : InspectionGadgetsFix() {
            @Nls
            @NotNull
            override fun getName(): String {
                return DeobfuscateToolBundle.message("inline.external.else.statement.quickfix")
            }

            @Nls
            @NotNull
            override fun getFamilyName(): String {
                return DeobfuscateToolBundle.message("inline.external.else.statement.family.quickfix")
            }

            override fun doFix(project: Project, descriptor: ProblemDescriptor) {
                val statement = PsiTreeUtil.getNonStrictParentOfType(descriptor.startElement, PsiIfStatement::class.java) ?: return
                val parent = statement.parent as? PsiStatement ?: return
                val parents = parent.parent.children
                val preceding = parents.copyOfRange(parents.indexOf(parent) + 2, parents.size - 2)
                val editor = DataManager.getInstance().dataContext.getData(CommonDataKeys.EDITOR)// FIXME deprecated
                InvertIfConditionAction().invoke(project, editor, statement.condition!!)
                val lastElse = parent.lastChild as PsiBlockStatement
                val innerIf = lastElse.codeBlock.children.last { it is PsiIfStatement }
                val emptyIf = innerIf.children.first { it is PsiBlockStatement } as PsiBlockStatement
                val space = emptyIf.codeBlock.children.last()
                preceding.forEach {
                    emptyIf.codeBlock.addBefore(it, space)
                }
                parent.parent.deleteChildRange(preceding.first(), preceding.last())
//                val outLinedElse = parent.children.firstOrNull { it is PsiKeyword && it.text == "else" }// TODO invoke InlineNestedElseInspection
            }
        }
    }
}
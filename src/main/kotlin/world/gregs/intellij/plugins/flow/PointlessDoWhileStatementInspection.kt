package world.gregs.intellij.plugins.flow

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.psi.*
import com.intellij.psi.PsiKeyword.FALSE
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import com.siyeh.ig.PsiReplacementUtil
import com.siyeh.ig.psiutils.CommentTracker
import com.siyeh.ig.style.ControlFlowStatementVisitorBase
import com.siyeh.ig.style.SingleStatementInBlockInspection
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NotNull
import world.gregs.intellij.plugins.DeobfuscateToolBundle

class PointlessDoWhileStatementInspection : SingleStatementInBlockInspection() {

    override fun getDisplayName(): String {
        return DeobfuscateToolBundle.message("pointless.do.while.statement.display.name")
    }

    public override fun buildErrorString(vararg infos: Any): String {
        return DeobfuscateToolBundle.message("pointless.do.while.statement.problem.descriptor")
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : ControlFlowStatementVisitorBase() {

            override fun visitLabeledStatement(statement: PsiLabeledStatement) {
                super.visitLabeledStatement(statement)
                val child = statement.lastChild
                if (child is PsiDoWhileStatement && child.isPointless() && !isNested(child)) {
                    registerStatementError(statement)
                }
            }

            override fun visitDoWhileStatement(statement: PsiDoWhileStatement) {
                super.visitDoWhileStatement(statement)
                if (statement.parent !is PsiLabeledStatement && statement.isPointless() && !isNested(statement)) {
                    registerStatementError(statement)
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

    private fun isNested(statement: PsiElement): Boolean = when (val parent = statement.parent) {
        is PsiMethod -> false
        is PsiDoWhileStatement -> true
        else -> isNested(parent)
    }

    private fun PsiDoWhileStatement.isPointless() = condition?.text.equals(FALSE)

    override fun buildFix(vararg infos: Any?) = object : InspectionGadgetsFix() {
        @Nls
        @NotNull
        override fun getName(): String {
            return DeobfuscateToolBundle.message("pointless.do.while.statement.quickfix")
        }

        @Nls
        @NotNull
        override fun getFamilyName(): String {
            return DeobfuscateToolBundle.message("pointless.do.while.statement.family.quickfix")
        }

        fun recursive(statement: PsiElement, list: MutableList<PsiBreakStatement>) {
            statement.children.forEach {
                if (it is PsiBreakStatement) {
                    list.add(it)
                }
                recursive(it, list)
            }
        }

        fun addDoWhileStatementsRecursive(statement: PsiElement, list: MutableList<PsiDoWhileStatement>) {
            for (child in statement.children) {
                if (child is PsiDoWhileStatement && child.isPointless()) {
                    list.add(child)
                }
                addDoWhileStatementsRecursive(child, list)
            }
        }

        fun fix(statement: PsiDoWhileStatement, factory: PsiElementFactory) {
            val label = (statement.parent as? PsiLabeledStatement)?.labelIdentifier
            val contents = statement.children.first { it is PsiBlockStatement }
            val code = contents.firstChild as PsiCodeBlock
            val ifStatement = code.children.first { it is PsiIfStatement } as PsiIfStatement
            val list = mutableListOf<PsiBreakStatement>()
            recursive(statement, list)

            val breaks = list.filter { b -> b.findExitedStatement() == statement || (label != null && b.children.any { child -> child is PsiIdentifier && child.text == label.text }) }
            val nested = isNested(statement)
            if (breaks.size == 1) {
                // +/- 2 to ignore spaces
                val parents = if (label == null) statement.parent.children else statement.parent.parent.children
                val contentsStart = parents.indexOf(if (label == null) statement else statement.parent) + 2
                val contentsEnd = parents.size - 2
                val preceding = if (contentsStart == parents.lastIndex) emptyArray() else parents.copyOfRange(contentsStart, contentsEnd)
                val b = breaks.first()
                if (preceding.isNotEmpty() && nested) {
                    // Replace break with all code preceding the do-while statement
                    val block = factory.createCodeBlock()
                    block.firstChild.delete()
                    block.lastChild.delete()
                    preceding.forEach { after ->
                        block.add(after)
                    }
                    b.replace(block)
                } else {
                    b.delete()
                }
            } else if (!nested) {
                // We're going to assume we can remove all the top most breaks without causing any issues.
                breaks.forEach {
                    it.delete()
                }
            }

            val root = if (label != null) statement.parent as PsiLabeledStatement else statement
            // Replace do while with contents of if statement combined into if else
            if (ifStatement.children.any { it is PsiKeyword && it.text == "else" }) {
                PsiReplacementUtil.replaceStatement(root, ifStatement.text)
            } else {
                val afterIfStatement = code.children.copyOfRange(code.children.indexOf(ifStatement) + 1, code.children.size - 2)// brace + whitespace
                if (afterIfStatement.isNotEmpty()) {
                    val ifElseStatement = StringBuilder()
                    ifElseStatement.append(ifStatement.text)
                    ifElseStatement.append("else {")
                    afterIfStatement.forEach {
                        ifElseStatement.append(it.text)
                    }
                    ifElseStatement.append("}")
                    PsiReplacementUtil.replaceStatement(root, ifElseStatement.toString())
                } else {
                    PsiReplacementUtil.replaceStatement(root, ifStatement.text)
                }
            }
        }

        override fun doFix(project: Project, descriptor: ProblemDescriptor) {
            val statement: PsiStatement = PsiTreeUtil.getNonStrictParentOfType(descriptor.startElement, PsiLabeledStatement::class.java)
                ?: PsiTreeUtil.getNonStrictParentOfType(descriptor.startElement, PsiDoWhileStatement::class.java)
                ?: return
            val labeled = statement is PsiLabeledStatement
            val loop = (if (labeled) statement.lastChild else statement) as PsiDoWhileStatement
            val list = mutableListOf<PsiDoWhileStatement>()
            list.add(loop)
            addDoWhileStatementsRecursive(loop, list)

            val factory = PsiElementFactory.getInstance(project)
            for (s in list.reversed()) {
                fix(s, factory)
            }
//            CodeStyleManager.getInstance(project).reformat(if(labeled) statement.parent else statement)
        }
    }
}
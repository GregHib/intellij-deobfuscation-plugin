package world.gregs.intellij.plugins.flow

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.JavaTokenType.*
import com.intellij.util.containers.ContainerUtil
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import com.siyeh.ig.PsiReplacementUtil
import com.siyeh.ig.psiutils.CommentTracker
import com.siyeh.ig.psiutils.ComparisonUtils
import org.jetbrains.annotations.NonNls
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import world.gregs.intellij.plugins.DeobfuscateUtil.isNumber

class InverseElseComparatorInspection : BaseInspection() {

    override fun getDisplayName(): String {
        return DeobfuscateToolBundle.message("inverse.else.comparator.display.name")
    }

    public override fun buildErrorString(vararg infos: Any): String {
        val expression = infos[0] as PsiExpression
        val statement = infos[1] as PsiIfStatement
        val ct = CommentTracker()
        val replacement = calculateReplacementExpression(statement, ct)
        return DeobfuscateToolBundle.message(
            "comparator.can.be.inverted.problem.descriptor",
            ct.text(expression),
            replacement
        )
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {
            override fun visitIfStatement(statement: PsiIfStatement) {
                super.visitIfStatement(statement)

                //Must have else block statement
                if(statement.elseBranch !is PsiBlockStatement) {
                    return
                }

                val expression = statement.condition as? PsiBinaryExpression
                    ?: return

                //Check expression has operands
                val left = expression.lOperand
                val right = expression.rOperand
                    ?: return

                //Check format matches expected
                val sign = expression.operationTokenType
                val invalid = when (sign) {
                    //Variable doesn't equal number or visa versa
                    NE -> left is PsiReferenceExpression && !isNumber(right) || isNumber(left) && right !is PsiReferenceExpression
                    //Both operands are binary expressions that have a range operation type
                    OROR -> left !is PsiBinaryExpression || right !is PsiBinaryExpression || !comparisonTokens.contains(left.operationTokenType) || !comparisonTokens.contains(right.operationTokenType)
                    //Both operands are binary expressions that both have a not-equal operators
                    ANDAND -> left !is PsiBinaryExpression || right !is PsiBinaryExpression || left.operationTokenType != NE || right.operationTokenType != NE
                    else -> true
                }

                //If format invalid; ignore
                if (invalid) {
                    return
                }

                registerError(statement, expression, statement)
            }
        }
    }

    public override fun buildFix(vararg infos: Any): InspectionGadgetsFix? {
        return object : InspectionGadgetsFix() {

            override fun getFamilyName(): String {
                return DeobfuscateToolBundle.message("inverse.else.comparator.invert.quickfix")
            }

            public override fun doFix(project: Project, descriptor: ProblemDescriptor) {
                val statement = descriptor.psiElement as PsiIfStatement

                val expression = statement.condition as? PsiBinaryExpression
                    ?: return

                val ct = CommentTracker()

                //Replace expression with negative comparator
                val expected = calculateReplacementExpression(statement, ct)
                PsiReplacementUtil.replaceExpression(expression, expected, ct)

                //Switch branches
                val branch = statement.thenBranch
                val elseBranch = statement.elseBranch?.copy() as? PsiStatement
                if(branch != null) {
                    statement.setElseBranch(branch)
                }
                if(elseBranch != null) {
                    statement.setThenBranch(elseBranch)
                }
            }
        }
    }

    @NonNls
    internal fun calculateReplacementExpression(statement: PsiIfStatement, ct: CommentTracker): String {
        val expression = statement.condition as? PsiBinaryExpression
            ?: return ""

        val sign = expression.operationTokenType
        return when (sign) {
            NE -> replaceOperation(expression, ComparisonUtils.getNegatedComparison(sign), ct)
            OROR, ANDAND -> {
                //Get operands as expressions
                val left = expression.lOperand as? PsiBinaryExpression
                    ?: return ""
                val right = expression.rOperand as? PsiBinaryExpression
                    ?: return ""
                //Replace expressions with negated operators
                val leftNew = replaceOperation(left, ComparisonUtils.getNegatedComparison(left.operationTokenType), ct)
                val rightNew = replaceOperation(right, ComparisonUtils.getNegatedComparison(right.operationTokenType), ct)
                //Change statement operator
                val opposite = when (sign) {
                    OROR -> "&&"
                    ANDAND -> "||"
                    else -> throw IllegalStateException()
                }
                "$leftNew $opposite $rightNew"
            }
            else -> ""
        }
    }

    /**
     * Replaces [expression] operator
     * @param expression The binary expression who's operator to replace
     * @param operator The new operator
     * @param ct CommentTracker
     * @return [expression] as a string with the new [operator]
     */
    private fun replaceOperation(expression: PsiBinaryExpression, operator: String, ct: CommentTracker): String {
        return "${ct.text(expression.lOperand)} $operator ${ct.text(expression.rOperand!!)}"
    }

    companion object {
        internal val comparisonTokens = ContainerUtil.immutableSet(LT, GT, LE, GE)
    }
}
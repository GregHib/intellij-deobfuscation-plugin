package world.gregs.intellij.plugins.flow

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.JavaTokenType.*
import com.intellij.psi.util.PsiUtil
import com.intellij.util.containers.ContainerUtil
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import com.siyeh.ig.PsiReplacementUtil
import com.siyeh.ig.psiutils.CommentTracker
import com.siyeh.ig.psiutils.ComparisonUtils
import org.jetbrains.annotations.NonNls
import world.gregs.intellij.plugins.DeobfuscateToolBundle

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
                if (statement.elseBranch !is PsiBlockStatement) {
                    return
                }

                val expression = statement.condition
                when (expression) {
                    is PsiPrefixExpression -> {
                        val child = expression.lastChild
                        //Check is exclamation boolean
                        if (expression.operationTokenType != EXCL || (child !is PsiReferenceExpression && child !is PsiParenthesizedExpression && child !is PsiMethodCallExpression)) {
                            return
                        }
                    }
                    is PsiPolyadicExpression -> {
                        //If binary do single & range check
                        if (expression is PsiBinaryExpression) {
                            //Check expression has operands
                            val left = expression.lOperand
                            val right = expression.rOperand
                                    ?: return

                            //Check both operands are binary expressions that have a range operation type
                            val sign = expression.operationTokenType
                            val valid = when (sign) {
                                //Single operand
                                NE -> true
                                //Both operands are binary expressions that have a range operation type
                                OROR -> left is PsiBinaryExpression && right is PsiBinaryExpression && comparisonTokens.contains(left.operationTokenType) && comparisonTokens.contains(right.operationTokenType)
                                else -> false
                            }

                            if (valid) {
                                registerError(statement, expression, statement)
                                return
                            }
                        }

                        //Check all operands are joined by '&&' and are negative
                        expression.operands.forEach {
                            val token = expression.getTokenBeforeOperand(it)?.tokenType
                            if (token == null || token == ANDAND) {
                                //Only continue if negated
                                if (it is PsiPrefixExpression && it.operationTokenType == EXCL) {
                                    return@forEach
                                } else if (it is PsiBinaryExpression && it.operationTokenType == NE) {
                                    return@forEach
                                }
                            }
                            return
                        }
                    }
                    else -> return
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

                val expression = statement.condition
                if (expression is PsiPrefixExpression || expression is PsiBinaryExpression) {
                    val ct = CommentTracker()

                    //Replace expression with negative comparator
                    val expected = calculateReplacementExpression(statement, ct)
                    if (expected.isEmpty()) {
                        throw IllegalStateException("Empty replacement for $statement $expression")
                    } else {
                        PsiReplacementUtil.replaceExpression(expression, expected, ct)
                    }

                    //Switch branches
                    val branch = statement.thenBranch
                    val elseBranch = statement.elseBranch?.copy() as? PsiStatement
                    if (branch != null) {
                        statement.setElseBranch(branch)
                    }
                    if (elseBranch != null) {
                        statement.setThenBranch(elseBranch)
                    }
                }
                return
            }
        }
    }

    @NonNls
    internal fun calculateReplacementExpression(statement: PsiIfStatement, ct: CommentTracker): String {
        val expression = statement.condition
        return when (expression) {
            is PsiPrefixExpression -> {
                var child = expression.lastChild
                if (child is PsiParenthesizedExpression) {
                    child = PsiUtil.deparenthesizeExpression(child)
                }
                ct.text(child)
            }
            is PsiPolyadicExpression -> {
                //If binary check single & range
                if (expression is PsiBinaryExpression) {
                    val sign = expression.operationTokenType
                    when (sign) {
                        NE -> return replaceOperation(expression, ComparisonUtils.getNegatedComparison(sign), ct)
                        OROR -> {
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
                            return "$leftNew $opposite $rightNew"
                        }
                    }
                }
                val builder = StringBuilder()
                //Apply each operand, negating operators and removing negative prefixes
                expression.operands.forEach {
                    //Apply logical operators
                    val token = expression.getTokenBeforeOperand(it)
                    if (token != null) {
                        builder.append(" || ")
                    }

                    //Apply operands
                    if (it is PsiPrefixExpression) {
                        //Ignore prefix
                        builder.append(ct.text(it.lastChild))
                    } else if (it is PsiBinaryExpression) {
                        //Negate relational operators
                        builder.append(replaceOperation(it, ComparisonUtils.getNegatedComparison(it.operationTokenType), ct))
                    }
                }
                builder.toString()
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
package world.gregs.intellij.plugins.bitwise

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.JavaTokenType.*
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.containers.ContainerUtil
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import com.siyeh.ig.psiutils.CommentTracker
import com.siyeh.ig.psiutils.ComparisonUtils
import com.siyeh.ig.psiutils.JavaPsiMathUtil
import org.jetbrains.annotations.NonNls
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import world.gregs.intellij.plugins.DeobfuscateUtil.isNumber

class PointlessBitwiseComparatorInspection : BaseInspection() {

    override fun getDisplayName(): String {
        return DeobfuscateToolBundle.message("pointless.bitwise.comparator.display.name")
    }

    public override fun buildErrorString(vararg infos: Any): String {
        val expression = infos[0] as PsiExpression
        val replacementExpression = calculateReplacementExpression(expression)
        return DeobfuscateToolBundle.message("expression.can.be.replaced.problem.descriptor", replacementExpression)
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    @NonNls
    internal fun calculateReplacementExpression(expression: PsiExpression): String {
        if (expression !is PsiBinaryExpression) {
            return ""
        }
        val operator = expression.operationSign
        val left = expression.lOperand
        val right = expression.rOperand ?: return ""

        val ct = CommentTracker()

        if (isBitwiseOperator(left) && isBitwiseOperator(right)) {
            val lOperand = getOperandText(left, ct)
            val rOperand = getOperandText(right, ct)
            if (lOperand != null && rOperand != null) {
                return "$rOperand ${ct.text(operator)} $lOperand"
            }
        } else if (isBitwiseOperator(left) && isNumber(right) || isNumber(left) && isBitwiseOperator(right)) {
            val flip = isNumber(left) && isBitwiseOperator(right)
            val number = JavaPsiMathUtil.getNumberFromLiteral(if (flip) left else right)
            val operand = getOperandText(if (flip) right else left, ct)
            if (number != null && operand != null) {
                val flipped = when (operator.tokenType) {
                    GT, GE, LT, LE -> ComparisonUtils.getFlippedComparison(operator.tokenType)
                    else -> ct.text(operator)
                }
                return "$operand ${if (flip) ct.text(operator) else flipped} ${decrementReverseValue(number)}"
            }
        } else {
            throw IllegalStateException("Didn't expect to see you here.")
        }
        return ""
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {

            override fun visitPolyadicExpression(expression: PsiPolyadicExpression) {
                super.visitPolyadicExpression(expression)
                val sign = expression.operationTokenType

                if (!comparisonTokens.contains(sign)) {
                    return
                }

                if (PsiUtilCore.hasErrorElementChild(expression)) {
                    return
                }

                val operands = expression.operands
                val parenthesized = operands.filter { it is PsiParenthesizedExpression || it is PsiPrefixExpression }

                //Must have at least one bitwise operator
                val bitwise = parenthesized.any { isBitwiseOperator(it) }
                if (!bitwise) {
                    return
                }

                //Either two bitwise or a bitwise and a number
                val bitwiseCount = parenthesized.count { isBitwiseOperator(it) }
                if (bitwiseCount != 2) {
                    val number = operands.any { isNumber(it) }

                    if (!number) {
                        return
                    }
                }

                registerError(expression, expression)
            }
        }
    }

    public override fun buildFix(vararg infos: Any): InspectionGadgetsFix {
        return object : InspectionGadgetsFix() {

            override fun getFamilyName(): String {
                return DeobfuscateToolBundle.message("pointless.bitwise.comparator.simplify.quickfix")
            }

            public override fun doFix(project: Project, descriptor: ProblemDescriptor) {
                val expression = descriptor.psiElement as PsiExpression
                val ct = CommentTracker()
                val newExpression = calculateReplacementExpression(expression)
                if (newExpression.isNotEmpty()) {
                    ct.replaceAndRestoreComments(expression, newExpression)
                }
            }
        }
    }

    companion object {
        internal val comparisonTokens = ContainerUtil.immutableSet(EQEQ, NE, LT, GT, LE, GE)

        private fun decrementReverseValue(number: Number): String {
            val decremented = -number.toLong() - 1
            return if (decremented > Int.MAX_VALUE || decremented < Int.MIN_VALUE) {
                "${decremented}L"
            } else {
                decremented.toString()
            }
        }

        private fun isHex(expression: PsiExpression?): Boolean {
            return expression != null && (expression.type == PsiTypes.intType() && expression.text == "0xffffffff" || expression.type == PsiTypes.longType() && expression.text == "0xffffffffffffffffL")
        }

        private fun isBitwiseOperator(express: PsiExpression?): Boolean {
            when (val expression = if (express is PsiParenthesizedExpression) PsiUtil.skipParenthesizedExprDown(express) else express) {
                is PsiPrefixExpression -> {
                    if (expression.operationTokenType == TILDE) {
                        return true
                    }
                }
                is PsiBinaryExpression -> {
                    val sign = expression.operationTokenType

                    if (sign != XOR) {
                        return false
                    }

                    if (isHex(expression.lOperand) || isHex(expression.rOperand)) {
                        return true
                    }
                }
            }
            return false
        }

        private fun getOperandText(expression: PsiExpression, ct: CommentTracker): String? {
            val express = PsiUtil.skipParenthesizedExprDown(expression) ?: return null
            val operand: PsiExpression? = when (express) {
                is PsiPrefixExpression -> {
                    var child = express.lastChild as? PsiExpression
                    if (child is PsiParenthesizedExpression) {
                        child = PsiUtil.skipParenthesizedExprDown(child)
                    }
                    PsiUtil.skipParenthesizedExprDown(child) ?: return null
                }
                is PsiBinaryExpression -> {
                    when {
                        isHex(express.lOperand) -> express.rOperand
                        isHex(express.rOperand) -> express.lOperand
                        else -> null
                    }
                }
                else -> null
            }
            //AND operators still require parentheses
            return if (operand is PsiPolyadicExpression && operand.operationTokenType == AND) {
                "(${ct.text(operand)})"
            } else if (operand != null) {
                ct.text(operand)
            } else {
                null
            }
        }
    }
}
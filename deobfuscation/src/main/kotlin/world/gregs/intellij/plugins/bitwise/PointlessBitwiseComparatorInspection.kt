package world.gregs.intellij.plugins.bitwise

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.JavaTokenType.*
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.containers.ContainerUtil
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import com.siyeh.ig.psiutils.CommentTracker
import com.siyeh.ig.psiutils.JavaPsiMathUtil
import org.jetbrains.annotations.NonNls
import world.gregs.intellij.plugins.DeobfuscateToolBundle

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
        val tokenType = expression.operationTokenType
        val left = expression.lOperand
        val right = expression.rOperand ?: return ""

        if(isBitwiseOperator(left) && isBitwiseOperator(right)) {
            val lOperand = getBitwiseOperandText(left)
            val rOperand = getBitwiseOperandText(right)
            if(lOperand != null && rOperand != null) {
                return "$rOperand ${getToken(tokenType)} $lOperand"
            }
        } else if(isBitwiseOperator(left) && isNumber(right) || isNumber(left) && isBitwiseOperator(right)) {
            val number = JavaPsiMathUtil.getNumberFromLiteral(if(isNumber(right)) right else left)
            val operand = getBitwiseOperandText(if(isBitwiseOperator(left)) left else right)
            if(number != null && operand != null) {
                val inverseToken = when(tokenType) {
                    GT -> LT
                    GE -> LE
                    LT -> GT
                    LE -> GE
                    else -> tokenType
                }
                return "$operand ${getToken(inverseToken)} ${decrementReverseValue(number)}"
            }
        } else {
            throw IllegalStateException("Didn't expect to see you here.")
        }
        return ""
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return PointlessBitwiseVisitor()
    }

    public override fun buildFix(vararg infos: Any): InspectionGadgetsFix? {
        return PointlessBitwiseFix()
    }

    private inner class PointlessBitwiseFix : InspectionGadgetsFix() {

        override fun getFamilyName(): String {
            return DeobfuscateToolBundle.message("pointless.bitwise.comparator.simplify.quickfix")
        }

        public override fun doFix(project: Project, descriptor: ProblemDescriptor) {
            val expression = descriptor.psiElement as PsiExpression
            val ct = CommentTracker()
            val newExpression = calculateReplacementExpression(expression)
            if (!newExpression.isEmpty()) {
                ct.replaceAndRestoreComments(expression, newExpression)
            }
        }
    }

    private inner class PointlessBitwiseVisitor : BaseInspectionVisitor() {

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
            val parenthesized = operands.filterIsInstance<PsiParenthesizedExpression>()
            val bitwise = parenthesized.any { isBitwiseOperator(it.expression) }

            if(!bitwise) {
                return
            }

            val bitwiseCount = parenthesized.count { isBitwiseOperator(it.expression) }
            if(bitwiseCount != 2) {
                val number = operands.any { isNumber(it) }

                if (!number) {
                    return
                }
            }

            registerError(expression, expression)
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

        private fun getToken(type: IElementType): String {
            return when(type) {
                GT -> ">"
                GE -> ">="
                LT -> "<"
                LE -> "<="
                EQEQ -> "=="
                NE -> "!="
                else -> ""
            }
        }

        private fun isHex(expression: PsiExpression?): Boolean {
            return expression != null && (expression.type == PsiType.INT && expression.text == "0xffffffff" || expression.type == PsiType.LONG && expression.text == "0xffffffffffffffffL")
        }

        private fun isNumber(expression: PsiExpression): Boolean {
            return JavaPsiMathUtil.getNumberFromLiteral(expression) != null
        }

        private fun isBitwiseOperator(expression: PsiExpression?): Boolean {
            val express = PsiUtil.skipParenthesizedExprDown(expression) as? PsiBinaryExpression ?: return false
            val sign = express.operationTokenType

            if(sign != XOR) {
                return false
            }

            if(isHex(express.lOperand) || isHex(express.rOperand)) {
                return true
            }

            return false
        }

        private fun getBitwiseOperandText(expression: PsiExpression): String? {
            val express = PsiUtil.skipParenthesizedExprDown(expression) as? PsiBinaryExpression ?: return null
            val op = when {
                isHex(express.lOperand) -> express.rOperand
                isHex(express.rOperand) -> express.lOperand
                else -> null
            }

            return if(op is PsiPolyadicExpression && op.operationTokenType == AND) {
                "(${op.text})"
            } else {
                op?.text
            }
        }
    }
}
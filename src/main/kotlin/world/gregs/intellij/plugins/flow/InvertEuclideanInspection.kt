package world.gregs.intellij.plugins.flow

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.JavaTokenType.ASTERISK
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilCore
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import com.siyeh.ig.psiutils.CommentTracker
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import java.math.BigInteger

class InvertEuclideanInspection : BaseInspection() {

    private val euclideanFields = mutableMapOf<String, EuclideanManager>()

    private fun registerField(field: PsiVariable): EuclideanManager {
        val name = field.name
        checkNotNull(name) { "Variable name is null." }
        var manager = euclideanFields[name]
        if (manager == null) {
            manager = EuclideanManager(field)
            euclideanFields[name] = manager
        }
        return manager
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {

            override fun visitField(field: PsiField?) {
                super.visitField(field)
                if (field == null || PsiUtilCore.hasErrorElementChild(field)) {
                    return
                }
                if (!field.isInt() && !field.isLong()) {
                    return
                }
                val literalExpression = field.children.filterIsInstance<PsiLiteralExpression>().firstOrNull() ?: return
                registerField(field).addAssignment(literalExpression)
            }

            override fun visitAssignmentExpression(expression: PsiAssignmentExpression?) {
                super.visitAssignmentExpression(expression)
                if (expression == null || PsiUtilCore.hasErrorElementChild(expression)) {
                    return
                }
                val leftExpression = expression.lExpression
                var rightExpression = expression.rExpression ?: return
                if (leftExpression !is PsiReferenceExpression) {
                    return
                }
                if (rightExpression is PsiPolyadicExpression || rightExpression is PsiParenthesizedExpression) {
                    rightExpression = PsiTreeUtil.findChildOfAnyType(rightExpression, PsiPrefixExpression::class.java, PsiLiteralExpression::class.java) ?: return
                }
                if (!validTypes.contains(rightExpression.type)) {
                    return
                }
                val field = leftExpression.resolve() as PsiVariable
                val value = rightExpression.toValue()
                if (!invertible(value, rightExpression.isLong())) {
                    return
                }
                registerField(field).addAssignment(rightExpression)
            }

            override fun visitPolyadicExpression(expression: PsiPolyadicExpression?) {
                super.visitPolyadicExpression(expression)
                if (expression == null || PsiUtilCore.hasErrorElementChild(expression)) {
                    return
                }
                val sign = expression.operationTokenType
                if (!comparisonTokens.contains(sign)) {
                    return
                }
                val referenceExpression = PsiTreeUtil.findChildOfType(expression, PsiReferenceExpression::class.java) ?: return
                if (referenceExpression.parent is PsiMethodCallExpression) {
                    return
                }
                val numberExpression = PsiTreeUtil.getChildrenOfAnyType(expression, PsiPrefixExpression::class.java, PsiLiteralExpression::class.java).firstOrNull() ?: return
                if (!validTypes.contains(numberExpression.type) && !validTypes.contains(referenceExpression.type)) {
                    return
                }
                val long = numberExpression.isLong()
                val value = numberExpression.toValue()
                if (!invertible(value, long)) {
                    return
                }
                val euclideanManager = euclideanFields[referenceExpression.referenceName] ?: return
                euclideanManager.addPolyadic(PolyadicExpression(expression, value))
                registerError(expression, expression)
            }
        }
    }

    override fun buildFix(vararg infos: Any?): InspectionGadgetsFix? {
        return object : InspectionGadgetsFix() {
            override fun getFamilyName(): String {
                return DeobfuscateToolBundle.message("inverse.euclidean.quickfix")
            }

            override fun doFix(project: Project, descriptor: ProblemDescriptor) {
                val expression = descriptor.psiElement as PsiExpression
                val field = PsiTreeUtil.findChildOfType(expression, PsiReferenceExpression::class.java) ?: return
                euclideanFields[field.referenceName]?.resolve()
            }
        }
    }

    override fun getDisplayName(): String {
        return DeobfuscateToolBundle.message("inverse.euclidean.display.name")
    }

    override fun buildErrorString(vararg infos: Any?): String {
        val expression = infos[0] as PsiExpression
        val replacementExpression = expression //TODO Calculate replacement expression
        return DeobfuscateToolBundle.message("inverse.euclidean.problem.descriptor", expression, replacementExpression)
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    companion object {
        private val validTypes = arrayOf(PsiType.INT, PsiType.LONG)
        private val comparisonTokens = arrayOf(ASTERISK)
        private val intModulus = BigInteger.ONE.shiftLeft(32)
        private val longModulus = BigInteger.ONE.shiftLeft(64)

        fun invertible(number: Long, long: Boolean): Boolean {
            return try {
                modInverse(number, long)
                true
            } catch (t: Throwable) {
                number < 0 && number < 1E7 || number > 0 && number > 1E7
            }
        }

        fun modInverse(field: PsiField): BigInteger? {
            val euclidean = field.text.toLongOrNull() ?: return null
            return modInverse(euclidean, field.isLong())
        }

        fun modInverse(expression: PsiExpression): BigInteger? {
            val euclidean = expression.text.toLongOrNull() ?: return null
            return modInverse(euclidean, expression.isLong())
        }

        fun modInverse(value: Long, long: Boolean): BigInteger {
            val a = BigInteger.valueOf(value)
            return a.modInverse(if (long) longModulus else intModulus)
        }

        fun gcd(vararg given: BigInteger): BigInteger? {
            var g = given[0].gcd(given[1])
            for (i in 2 until given.size) {
                g = g.gcd(given[i])
            }
            return g
        }

        fun replaceEuclideanField(field: PsiField, realValue: Int): String {
            return realValue.toString()
        }

        fun replacePolyadicExpression(pair: PolyadicExpression, successor: Long): String {
            val remove = pair.value == successor
            if (remove) {
                return pair.expression.text.replace("*", "").replace(successor.toString(), "").trim()
            }
            //TODO Folded values like *= -18289330
            return "wtf"
        }
    }

    inner class PolyadicExpression(val expression: PsiPolyadicExpression, val value: Long)

    inner class EuclideanManager(val field: PsiVariable) {

        private val assignments = mutableListOf<PsiExpression>()
        private val polyadics = mutableListOf<PolyadicExpression>()

        fun addAssignment(expression: PsiExpression) {
            assignments.add(expression)
        }

        fun addPolyadic(pair: PolyadicExpression) {
            polyadics.add(pair)
        }

        fun resolve() {
            val multiplier = solveMultiplier() ?: return
            for (i in assignments) {
                val assignmentValue = i.toValue()
                val realValue = if (i.isLong()) {
                    assignmentValue * multiplier
                } else {
                    (assignmentValue.toInt() * multiplier.toInt()).toLong()
                }
                CommentTracker().replaceAndRestoreComments(i, realValue.toString())
            }
            val multiplierString = multiplier.toString()
            for (i in polyadics) {
                val expression = i.expression
                val operator = PsiTreeUtil.getChildOfType(expression, PsiJavaToken::class.java)
                checkNotNull(operator) { "Operator is null." }
                val replacement = expression.text.replace(operator.text, "").replace(multiplierString, "").trim()
                CommentTracker().replaceAndRestoreComments(i.expression, replacement)
            }
        }

        private fun solveMultiplier(): Long? {
            for (a in assignments) {
                val assignmentValue = a.toValue()
                for (i in polyadics) {
                    val long = i.expression.isLong()
                    val prefixValue = i.value
                    val realValue = if (long) {
                        assignmentValue * prefixValue
                    } else {
                        (assignmentValue.toInt() * prefixValue.toInt()).toLong()
                    }
                    val inverted = modInverse(prefixValue, long)
                    val validator = if (long) {
                        inverted.toLong() * realValue
                    } else {
                        (inverted.toInt() * realValue.toInt()).toLong()
                    }
                    if (validator == assignmentValue) {
                        return prefixValue
                    }
                }
            }
            return null
        }
    }

}

fun PsiExpression.isInt(): Boolean {
    return type == PsiType.INT
}
fun PsiExpression.isLong(): Boolean {
    return type == PsiType.LONG
}
fun PsiExpression.toValue(): Long {
    return if (isLong()) {
        text.toLong()
    } else {
        text.toInt().toLong()
    }
}
fun PsiVariable.isInt(): Boolean {
    return type == PsiType.INT
}
fun PsiVariable.isLong(): Boolean {
    return type == PsiType.LONG
}
package world.gregs.intellij.plugins

import com.intellij.psi.PsiExpression
import com.siyeh.ig.psiutils.JavaPsiMathUtil

object DeobfuscateUtil {
    fun isNumber(expression: PsiExpression): Boolean {
        return JavaPsiMathUtil.getNumberFromLiteral(expression) != null
    }
}
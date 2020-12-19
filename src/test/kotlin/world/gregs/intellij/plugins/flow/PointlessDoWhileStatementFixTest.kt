package world.gregs.intellij.plugins.flow

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import world.gregs.intellij.plugins.LightQuickFixTester

internal class PointlessDoWhileStatementFixTest : LightQuickFixTester() {
    override val myDefaultHint = DeobfuscateToolBundle.message("pointless.do.while.statement.quickfix")

    fun testDoWhileStatement() {
        doTest()
    }

    fun testInlineBlocklessDoWhileStatement() {
        doTest()
    }

    fun testInlineNamedDoWhileStatement() {
        doTest()
    }

    fun testInlineNestedDoWhileStatement() {
        doTest()
    }

    fun testInlineMultiNestedDoWhileStatement() {
        doTest()
    }

    override fun getInspection(): BaseInspection {
        return PointlessDoWhileStatementInspection()
    }
}
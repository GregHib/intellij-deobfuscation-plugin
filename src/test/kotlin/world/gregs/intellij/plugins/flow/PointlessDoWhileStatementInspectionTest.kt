package world.gregs.intellij.plugins.flow

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.LightInspectionTester

internal class PointlessDoWhileStatementInspectionTest : LightInspectionTester() {

    fun testDoWhileStatement() {
        doTest()
    }

    override fun getInspection(): BaseInspection {
        return PointlessDoWhileStatementInspection()
    }
}
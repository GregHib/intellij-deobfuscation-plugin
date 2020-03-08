package world.gregs.intellij.plugins.flow

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.LightInspectionTester

internal class InlineExternalElseStatementInspectionTest : LightInspectionTester() {

    fun testToDo() {
        doTest()
    }

    override fun getInspection(): BaseInspection {
        return InlineExternalElseStatementInspection()
    }
}
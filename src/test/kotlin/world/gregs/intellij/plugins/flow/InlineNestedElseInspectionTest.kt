package world.gregs.intellij.plugins.flow

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.LightInspectionTester

internal class InlineNestedElseInspectionTest : LightInspectionTester() {

    fun testNestedElse() {
        doTest()
    }

    override fun getInspection(): BaseInspection? {
        return InlineNestedElseInspection()
    }
}
package world.gregs.intellij.plugins.flow

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.LightInspectionTester

internal class InverseElseComparatorInspectionTest : LightInspectionTester() {

    fun testInverseElseComparator() {
        doTest()
    }

    fun testInverseBooleanComparator() {
        doTest()
    }

    fun testInverseMethodComparator() {
        doTest()
    }

    fun testInverseRangeComparator() {
        doTest()
    }

    override fun getInspection(): BaseInspection {
        return InverseElseComparatorInspection()
    }
}
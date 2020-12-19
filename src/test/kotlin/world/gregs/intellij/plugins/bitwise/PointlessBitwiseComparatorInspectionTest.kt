package world.gregs.intellij.plugins.bitwise

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.LightInspectionTester

internal class PointlessBitwiseComparatorInspectionTest : LightInspectionTester() {

    fun testPointlessBitwiseComparator() {
        doTest()
    }

    fun testTildeComparator() {
        doTest()
    }

    fun testIgnoredComparators() {
        doTest()
    }

    fun testLessThanBitwiseComparator() {
        doTest()
    }

    fun testLessThanEqualBitwiseComparator() {
        doTest()
    }

    fun testGreaterThanBitwiseComparator() {
        doTest()
    }

    fun testGreaterThanEqualBitwiseComparator() {
        doTest()
    }

    fun testEqualsBitwiseComparator() {
        doTest()
    }

    fun testNotEqualsBitwiseComparator() {
        doTest()
    }

    override fun getInspection(): BaseInspection {
        return PointlessBitwiseComparatorInspection()
    }
}
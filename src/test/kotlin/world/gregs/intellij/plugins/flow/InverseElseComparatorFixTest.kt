package world.gregs.intellij.plugins.flow

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import world.gregs.intellij.plugins.LightQuickFixTester

internal class InverseElseComparatorFixTest : LightQuickFixTester() {
    override val myDefaultHint = DeobfuscateToolBundle.message("inverse.else.comparator.invert.quickfix")

    fun testInvertSingleElseComparator() {
        doTest()
    }

    fun testInvertDoubleElseComparator() {
        doTest()
    }

    fun testInvertMethodElseComparator() {
        doTest()
    }

    fun testInvertRangeElseComparator() {
        doTest()
    }

    override fun getInspection(): BaseInspection? {
        return InverseElseComparatorInspection()
    }
}
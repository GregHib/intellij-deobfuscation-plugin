package world.gregs.intellij.plugins.bitwise

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import world.gregs.intellij.plugins.LightQuickFixTester

internal class PointlessBitwiseComparatorFixTest : LightQuickFixTester() {

    override val myDefaultHint = DeobfuscateToolBundle.message("pointless.bitwise.comparator.simplify.quickfix")

    fun testRemoveBitwiseAndComparator() {
        doTest()
    }

    fun testRemoveBitwiseComparator() {
        doTest()
    }

    fun testRemoveComplimentAndComparator() {
        doTest()
    }

    fun testRemoveComplimentComparator() {
        doTest()
    }

    fun testRemoveFlippedBitwiseComparator() {
        doTest()
    }

    override fun getInspection(): BaseInspection {
        return PointlessBitwiseComparatorInspection()
    }

}
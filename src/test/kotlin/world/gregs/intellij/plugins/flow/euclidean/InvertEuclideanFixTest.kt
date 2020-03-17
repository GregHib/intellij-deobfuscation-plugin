package world.gregs.intellij.plugins.flow.euclidean

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import world.gregs.intellij.plugins.LightQuickFixTester
import world.gregs.intellij.plugins.flow.InvertEuclideanInspection

class InvertEuclideanFixTest : LightQuickFixTester() {

    override val myDefaultHint = DeobfuscateToolBundle.message("inverse.euclidean.quickfix")

    fun testInvertEuclideanField() {
        doTest()
    }

    fun testInvertEuclideanConstructorField() {
        doTest()
    }

    fun testInvertEuclideanStaticInit() {
        doTest()
    }

    fun testInvertEuclideanMultiAssignment() {
        doTest()
    }

    fun testInvertEuclideanOperators() {
        doTest()
    }

    fun testInvertEuclideanMethodReference() {
        doTest()
    }

    override fun getInspection(): BaseInspection? {
        return InvertEuclideanInspection()
    }

}
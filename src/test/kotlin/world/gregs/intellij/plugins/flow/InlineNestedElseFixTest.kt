package world.gregs.intellij.plugins.flow

import com.siyeh.ig.BaseInspection
import world.gregs.intellij.plugins.DeobfuscateToolBundle
import world.gregs.intellij.plugins.LightQuickFixTester

internal class InlineNestedElseFixTest : LightQuickFixTester() {
    override val myDefaultHint = DeobfuscateToolBundle.message("inline.nested.else.quickfix")

    fun testInlineNestedElse() {
        doTest()
    }

    fun testInlineNestedElseBoolean() {
        doTest()
    }

    fun testInlineNestedElseMethod() {
        doTest()
    }

    override fun getInspection(): BaseInspection {
        return InlineNestedElseInspection()
    }
}
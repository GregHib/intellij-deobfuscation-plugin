package world.gregs.intellij.plugins.flow

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

internal class InlineNestedElseInspectionTest : LightCodeInsightFixtureTestCase() {
    companion object {
        private const val fileName = "NestedElse.java"
    }

    override fun getTestDataPath(): String {
        val path = InlineNestedElseInspectionTest::class.java.getResource(fileName).path
        return path.substring(0, path.length - fileName.length)
    }

    fun testNestedElse() {
        myFixture.enableInspections(InlineNestedElseInspection::class.java)
        myFixture.testHighlighting(true, false, false, fileName)
    }
}
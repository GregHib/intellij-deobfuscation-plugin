package world.gregs.intellij.plugins.flow

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

internal class InverseElseComparatorInspectionTest : LightCodeInsightFixtureTestCase() {
    companion object {
        private const val fileName = "InverseElseComparator.java"
    }

    override fun getTestDataPath(): String {
        val path = InverseElseComparatorInspectionTest::class.java.getResource(fileName).path
        return path.substring(0, path.length - fileName.length)
    }

    fun testInverseElseComparator() {
        myFixture.enableInspections(InverseElseComparatorInspection::class.java)
        myFixture.testHighlighting(true, false, false, fileName)
    }
}
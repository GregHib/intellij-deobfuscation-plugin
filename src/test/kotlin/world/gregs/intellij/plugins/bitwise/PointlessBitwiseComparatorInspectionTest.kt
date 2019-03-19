package world.gregs.intellij.plugins.bitwise

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase


internal class PointlessBitwiseComparatorInspectionTest : LightCodeInsightFixtureTestCase() {
    companion object {
        private const val fileName = "inspection.java"
    }

    override fun getTestDataPath(): String {
        val path = PointlessBitwiseComparatorInspectionTest::class.java.getResource(fileName).path
        return path.substring(0, path.length - fileName.length)
    }

    fun testPointlessBitwiseComparator() {
        myFixture.enableInspections(PointlessBitwiseComparatorInspection::class.java)
        myFixture.testHighlighting(true, false, false, fileName)
    }
}
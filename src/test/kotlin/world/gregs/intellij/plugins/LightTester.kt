package world.gregs.intellij.plugins

import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.openapi.util.text.StringUtil
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase
import com.siyeh.ig.BaseInspection
import org.junit.Assert
import java.nio.file.FileSystems

abstract class LightTester(private val fix: Boolean) : JavaCodeInsightFixtureTestCase() {

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        val inspections = getInspections()
        if (inspections != null) {
            myFixture.enableInspections(*inspections)
        }
    }

    private fun getInspections(): Array<BaseInspection>? {
        val inspection = getInspection()
        return inspection?.let { arrayOf(it) }
    }

    abstract fun getInspection(): BaseInspection?

    override fun getTestDataPath(): String {
        return "${FileSystems.getDefault().getPath("").toAbsolutePath()}/src/test/resources/${if (fix) "fixes" else "test"}/$basePath"
    }

    override fun getBasePath(): String {
        val className = getInspectionClass().name
        val words = className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val basePath = StringBuilder()
        val lastWordIndex = words.size - 1
        for (i in 0 until lastWordIndex) {
            basePath.append(words[i]).append('/')
        }
        var lastWord = words[lastWordIndex]
        lastWord = StringUtil.trimEnd(lastWord, "Inspection")
        val length = lastWord.length
        var upperCase = false
        for (i in 0 until length) {
            val ch = lastWord[i]
            if (Character.isUpperCase(ch)) {
                if (!upperCase) {
                    upperCase = true
                    if (i != 0) {
                        basePath.append('_')
                    }
                }
                basePath.append(Character.toLowerCase(ch))
            } else {
                upperCase = false
                basePath.append(ch)
            }
        }
        return basePath.toString()
    }

    protected fun getInspectionClass(): Class<out InspectionProfileEntry> {
        val inspection = getInspection()
        Assert.assertNotNull("File-based tests should either return an inspection or override this method", inspection)
        return inspection!!.javaClass
    }

}
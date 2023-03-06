package world.gregs.intellij.plugins

import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInsight.daemon.HighlightDisplayKey
import com.intellij.profile.codeInspection.ProjectInspectionProfileManager
import com.intellij.testFramework.UsefulTestCase
import org.intellij.lang.annotations.Language
import org.junit.Assert

/**
 * Clone of [LightInspectionTestCase]
 * @author Bas Leijdekkers
 */
abstract class LightInspectionTester : LightTester(false) {

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        val inspection = getInspection()
        if (inspection != null) {
            val project = myFixture.project
            val displayKey = HighlightDisplayKey.find(inspection.shortName)
            val currentProfile = ProjectInspectionProfileManager.getInstance(project).currentProfile
            val errorLevel = currentProfile.getErrorLevel(displayKey, null)
            if (errorLevel === HighlightDisplayLevel.DO_NOT_SHOW) {
                currentProfile.setErrorLevel(displayKey, HighlightDisplayLevel.WARNING, project)
            }
        }
    }

    protected fun addEnvironmentClass(@Language("JAVA") classText: String) {
        myFixture.addClass(classText)
    }

    protected fun doStatementTest(@Language(value = "JAVA", prefix = "@SuppressWarnings(\"all\") class X { void m() {", suffix = "}}") statementText: String) {
        doTest("class X { void m() {$statementText}}")
    }

    protected fun doMemberTest(@Language(value = "JAVA", prefix = "@SuppressWarnings(\"all\") class X {", suffix = "}") memberText: String) {
        doTest("class X {$memberText}")
    }

    protected fun doTest(@Language("JAVA") classText: String) {
        doTest(classText, "X.java")
    }

    protected fun assertQuickFixNotAvailable(name: String) {
        UsefulTestCase.assertEmpty(myFixture.filterAvailableIntentions(name))
    }

    protected fun checkQuickFix(name: String, @Language("JAVA") result: String) {
        val intention = myFixture.getAvailableIntention(name)
        Assert.assertNotNull(intention)
        myFixture.launchAction(intention!!)
        myFixture.checkResult(result)
    }

    protected fun checkQuickFix(intentionName: String) {
        val intention = myFixture.getAvailableIntention(intentionName)
        Assert.assertNotNull(intention)
        myFixture.launchAction(intention!!)
        myFixture.checkResultByFile(getTestName(false) + ".after.java")
    }

    protected fun doTest(@Language("JAVA") classText: String, fileName: String) {
        val newText = StringBuilder()
        var start = 0
        var end = classText.indexOf("/*")
        while (end >= 0) {
            newText.append(classText, start, end)
            start = end + 2
            end = classText.indexOf("*/", end)
            if (end < 0) {
                throw IllegalArgumentException("invalid class text")
            }
            val text = classText.substring(start, end)
            when {
                text.isEmpty() -> newText.append("</warning>")
                "!" == text -> newText.append("</error>")
                "_" == text -> newText.append("<caret>")
                text.startsWith("!") -> newText.append("<error descr=\"").append(text.substring(1)).append("\">")
                text.startsWith(" ") -> newText.append("/*").append(text).append("*/")
                else -> newText.append("<warning descr=\"").append(text).append("\">")
            }
            start = end + 2
            end = classText.indexOf("/*", end + 1)
        }
        newText.append(classText, start, classText.length)
        myFixture.configureByText(fileName, newText.toString())
        myFixture.testHighlighting(true, false, false)
    }

    protected fun doTest() {
        doNamedTest(getTestName(false))
    }

    protected fun doNamedTest(name: String) {
        myFixture.configureByFile("$name.java")
        myFixture.testHighlighting(true, false, false)
    }

}
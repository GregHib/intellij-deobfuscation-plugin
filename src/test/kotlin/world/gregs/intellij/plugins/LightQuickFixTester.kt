package world.gregs.intellij.plugins

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder
import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.NonNls
import org.junit.Assert
import java.nio.file.FileSystems

/**
 * Clone of [com.siyeh.ig.IGQuickFixesTestCase]
 * @author anna
 */
abstract class LightQuickFixTester : LightTester(true) {
    abstract val myDefaultHint: String

    @Throws(Exception::class)
    override fun tuneFixture(builder: JavaModuleFixtureBuilder<*>?) {
        builder!!.setLanguageLevel(LanguageLevel.JDK_1_8)
    }

    override fun getTestDataPath(): String {
        return "${FileSystems.getDefault().getPath("").toAbsolutePath()}/src/test/resources/fixes/$basePath"
    }

    protected fun assertQuickfixNotAvailable() {
        assertQuickfixNotAvailable(myDefaultHint)
    }

    protected fun assertQuickfixNotAvailable(quickfixName: String?) {
        val testName = getTestName(false)
        myFixture.configureByFile("$testName.java")
        UsefulTestCase.assertEmpty("Quickfix \'$quickfixName\' is available but should not",
                myFixture.filterAvailableIntentions(quickfixName!!))
    }

    protected fun assertQuickfixNotAvailable(quickfixName: String, @Language("JAVA") @NonNls text: String) {
        var text = text
        text = text.replace("/**/", "<caret>")
        myFixture.configureByText(JavaFileType.INSTANCE, text)
        UsefulTestCase.assertEmpty("Quickfix \'$quickfixName\' is available but should not",
                myFixture.filterAvailableIntentions(quickfixName))
    }

    protected fun doTest() {
        val testName = getTestName(false)
        doTest(testName, myDefaultHint)
    }

    protected fun doTest(hint: String) {
        val testName = getTestName(false)
        doTest(testName, hint)
    }

    protected fun doTest(testName: String, hint: String) {
        myFixture.configureByFile("$testName.java")
        val action = myFixture.getAvailableIntention(hint)
        Assert.assertNotNull(action)
        myFixture.launchAction(action!!)
        myFixture.checkResultByFile("$testName.after.java")
    }

    protected fun doExpressionTest(
            hint: String,
            @Language(value = "JAVA", prefix = "/** @noinspection ALL*/class \$X$ {static {System.out.print(", suffix = ");}}") @NonNls before: String,
            @Language(value = "JAVA", prefix = "class \$X$ {static {System.out.print(", suffix = ");}}") @NonNls after: String) {
        doTest(hint, "class \$X$ {static {System.out.print($before);}}", "class \$X$ {static {System.out.print($after);}}")
    }

    protected fun doMemberTest(
            hint: String,
            @Language(value = "JAVA", prefix = "/** @noinspection ALL*/class \$X$ {", suffix = "}") @NonNls before: String,
            @Language(value = "JAVA", prefix = "class \$X$ {", suffix = "}") @NonNls after: String) {
        doTest(hint, "class \$X$ {$before}", "class \$X$ {$after}")
    }

    protected fun doTest(hint: String,
                         @Language("JAVA") @NonNls before: String,
                         @Language("JAVA") @NonNls after: String) {
        doTest(hint, before, after, "aaa.java")
    }

    protected fun doTest(hint: String,
                         @Language("JAVA") @NonNls before: String,
                         @Language("JAVA") @NonNls after: String,
                         fileName: String) {
        var before = before
        before = before.replace("/**/", "<caret>")
        myFixture.configureByText(fileName, before)
        val intention = myFixture.getAvailableIntention(hint)
        Assert.assertNotNull(intention)
        myFixture.launchAction(intention!!)
        myFixture.checkResult(after)
    }

}
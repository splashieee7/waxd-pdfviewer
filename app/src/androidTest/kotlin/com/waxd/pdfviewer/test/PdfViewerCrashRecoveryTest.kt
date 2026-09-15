package com.waxd.pdfviewer.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.waxd.pdfviewer.testrules.RetryRules
import com.waxd.pdfviewer.RetryableComposeRule
import com.waxd.pdfviewer.crashed
import com.waxd.pdfviewer.testrules.OrientationRules
import com.waxd.pdfviewer.util.PdfViewerLauncher
import com.waxd.pdfviewer.util.PdfViewerRobot
import com.waxd.pdfviewer.util.PdfViewerTestUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Whole WebView crash recovery flow.
 */
@RunWith(AndroidJUnit4::class)
class PdfViewerCrashRecoveryTest {

    private val composeRule = RetryableComposeRule()

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(RetryRules())
        .around(OrientationRules())
        .around(composeRule)

    private val robot = PdfViewerRobot(composeRule)

    @Before
    fun setup() {
        PdfViewerTestUtils.init(composeRule)
    }

    @Test
    fun reloadButton_dismissesCrashUiAndRecovers() {
        PdfViewerLauncher.launchDefault().use { scenario ->
            scenario.onActivity { it.crashed = true }
            scenario.recreate()

            robot.assertCrashUiVisible()
            robot.clickReload()

            PdfViewerTestUtils.waitForCrashUiDismissed(scenario)

            robot.assertCrashUiHidden()
            robot.assertWebViewVisible()
        }
    }
}

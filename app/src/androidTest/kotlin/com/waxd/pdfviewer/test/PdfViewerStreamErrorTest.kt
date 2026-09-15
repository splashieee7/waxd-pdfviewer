package com.waxd.pdfviewer.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.waxd.pdfviewer.testrules.RetryRules
import com.waxd.pdfviewer.RetryableComposeRule
import com.waxd.pdfviewer.documentProperties
import com.waxd.pdfviewer.testrules.OrientationRules
import com.waxd.pdfviewer.totalPages
import com.waxd.pdfviewer.util.PdfViewerLauncher
import com.waxd.pdfviewer.util.PdfViewerRobot
import com.waxd.pdfviewer.util.PdfViewerTestUtils
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PdfViewerStreamErrorTest {

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
    fun loadPdf_streamFailsMidRead_showsError() {
        PdfViewerLauncher.launchWithFailingMidRead(
            "test-multipage.pdf",
            bytesBeforeFailure = 1024
        ).use { scenario ->
            PdfViewerTestUtils.waitForSnackbar(PdfViewerRobot.SnackbarMessage.FileOpenError)

            scenario.onActivity {
                assertNull(it.documentProperties)
                assertEquals(0, it.totalPages)
            }

            robot.assertWebViewVisible()
            robot.assertCrashUiHidden()
        }
    }
}

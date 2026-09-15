package com.waxd.pdfviewer.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.waxd.pdfviewer.testrules.RetryRules
import com.waxd.pdfviewer.RetryableComposeRule
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
 * Toolbar toggle by tap.
 */
@RunWith(AndroidJUnit4::class)
class PdfViewerToolbarToggleTest {

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
    fun tapWebView_hidesToolbar() {
        PdfViewerLauncher.launchWithTestAsset("test-simple.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForCanvasRendered(scenario)

            robot.assertToolbarVisible(scenario)

            robot.tapWebView()
            PdfViewerTestUtils.waitForToolbarState(scenario, visible = false)
            robot.assertToolbarHidden(scenario)
        }
    }

    @Test
    fun tapWebView_whenHidden_showsToolbar() {
        PdfViewerLauncher.launchWithTestAsset("test-simple.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForCanvasRendered(scenario)

            robot.tapWebView()
            PdfViewerTestUtils.waitForToolbarState(scenario, visible = false)

            robot.tapWebView()
            PdfViewerTestUtils.waitForToolbarState(scenario, visible = true)
            robot.assertToolbarVisible(scenario)
        }
    }

    @Test
    fun tapWebView_whenTextSelected_doesNotToggleToolbar() {
        PdfViewerLauncher.launchWithTestAsset("test-simple.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForCanvasRendered(scenario)
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Test Text")

            PdfViewerTestUtils.selectAllText(scenario)

            robot.assertToolbarVisible(scenario)
            robot.tapWebView()
            PdfViewerTestUtils.assertToolbarStableVisibility(
                scenario, expectedVisible = true
            )
        }
    }
}

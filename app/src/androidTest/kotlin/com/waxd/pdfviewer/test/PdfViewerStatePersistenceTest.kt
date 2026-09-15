package com.waxd.pdfviewer.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.waxd.pdfviewer.testrules.RetryRules
import com.waxd.pdfviewer.RetryableComposeRule
import com.waxd.pdfviewer.crashed
import com.waxd.pdfviewer.currentPage
import com.waxd.pdfviewer.testrules.OrientationRules
import com.waxd.pdfviewer.util.PdfViewerLauncher
import com.waxd.pdfviewer.util.PdfViewerRobot
import com.waxd.pdfviewer.util.PdfViewerTestUtils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Configuration change and state persistence
 */
@RunWith(AndroidJUnit4::class)
class PdfViewerStatePersistenceTest {

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
    fun pageNumber_survivesRecreation() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)

            scenario.onActivity {
                it.currentPage = 3
            }

            scenario.recreate()
            scenario.onActivity {
                assertEquals(3, it.currentPage)
            }
        }
    }

    @Test
    fun crashState_survivesRecreation_showsCrashUi() {
        PdfViewerLauncher.launchDefault().use { scenario ->
            scenario.onActivity {
                it.crashed = true
            }

            scenario.recreate()
            robot.assertCrashUiVisible()
        }
    }

    @Test
    fun documentReloads_afterRecreation() {
        PdfViewerLauncher.launchWithTestAsset("test-simple.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)

            scenario.onActivity {
                assertEquals(1, it.currentPage)
            }

            scenario.recreate()
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            scenario.onActivity {
                assertEquals(1, it.currentPage)
            }
        }
    }
}

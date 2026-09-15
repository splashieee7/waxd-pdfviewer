package com.waxd.pdfviewer.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.waxd.pdfviewer.testrules.RetryRules
import com.waxd.pdfviewer.RetryableComposeRule
import com.waxd.pdfviewer.currentPage
import com.waxd.pdfviewer.outlineStatus
import com.waxd.pdfviewer.testrules.OrientationRules
import com.waxd.pdfviewer.totalPages
import com.waxd.pdfviewer.util.PdfViewerLauncher
import com.waxd.pdfviewer.util.PdfViewerRobot
import com.waxd.pdfviewer.util.PdfViewerRobot.AppMenuItem
import com.waxd.pdfviewer.util.PdfViewerTestUtils
import com.waxd.pdfviewer.viewModel.PdfViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Tests multipage PDF with navigation rendering, page count, and outline.
 */
@RunWith(AndroidJUnit4::class)
class PdfViewerMultiPageRenderTest {

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

    // Activity state

    @Test
    fun documentLoad_setsCorrectPageCount() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)

            scenario.onActivity {
                assertEquals(4, it.totalPages)
            }
        }
    }

    // Navigation rendering

    @Test
    fun navigateToNextPage_rendersNewContent() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForCanvasRendered(scenario)
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page One Content")

            robot.clickNext()

            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page Two Content")
            robot.assertBridgePage(scenario, 2)
        }
    }

    @Test
    fun navigateBackToPreviousPage_rendersOriginalContent() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForCanvasRendered(scenario)
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page One Content")

            robot.clickNext()
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page Two Content")

            robot.clickPrevious()
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page One Content")
            robot.assertBridgePage(scenario, 1)
        }
    }

    @Test
    fun navigateFirstAndLast_rendersCorrectContent() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForCanvasRendered(scenario)
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page One Content")

            robot.click(AppMenuItem.Last)
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page Four Content")
            robot.assertBridgePage(scenario, 4)

            robot.click(AppMenuItem.First)
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page One Content")
            robot.assertBridgePage(scenario, 1)
        }
    }

    @Test
    fun navigationButtonStates_updateAfterRenderedNavigation() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForCanvasRendered(scenario)

            robot.assertNavigationState(previousEnabled = false, nextEnabled = true)

            robot.click(AppMenuItem.Last)
            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page Four Content")

            robot.assertNavigationState(previousEnabled = true, nextEnabled = false)
        }
    }

    // Outline

    @Test
    fun documentWithOutline_outlineIsAvailable() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForOutlineAvailable(scenario)

            scenario.onActivity {
                assertTrue("hasOutline should be true", it.viewModel.hasOutline())
                assertEquals(
                    PdfViewModel.OutlineStatus.Available,
                    it.outlineStatus
                )
            }

            robot.assertMenuItemVisible(AppMenuItem.Outline, expected = true)
        }
    }

    @Test
    fun outlineRequest_producesLoadedEntries() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForOutlineAvailable(scenario)

            robot.requestOutline(scenario)
            PdfViewerTestUtils.waitForOutlineLoaded(scenario)

            val outlineSize = robot.getLoadedOutlineSize(scenario)
            assertEquals(
                "Outline should have 4 entries (one per section)",
                4, outlineSize
            )
        }
    }

    @Test
    fun outlineNavigation_tapEntry_navigatesAndRenders() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForCanvasRendered(scenario)
            PdfViewerTestUtils.waitForOutlineAvailable(scenario)

            robot.openOutlineFragment()
            robot.waitForOutlineEntries()
            robot.clickOutlineEntry(1)

            PdfViewerTestUtils.assertTextLayerContent(scenario, "Page Two Content")
            robot.assertBridgePage(scenario, 2)

            scenario.onActivity {
                assertEquals(2, it.currentPage)
            }
        }
    }

    @Test
    fun outlineFragment_dismissesOnBackPress() {
        PdfViewerLauncher.launchWithTestAsset("test-multipage.pdf").use { scenario ->
            PdfViewerTestUtils.waitForDocumentFullyLoaded(scenario)
            PdfViewerTestUtils.waitForOutlineAvailable(scenario)

            robot.openOutlineFragment()
            robot.waitForOutlineEntries()

            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
            robot.waitForOutlineFragmentDismissed()
        }
    }
}

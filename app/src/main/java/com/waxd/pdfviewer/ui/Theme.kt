package com.waxd.pdfviewer.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Waxd terminal/ops-console theme -- same four hex values as every other waxd
// reskin. This app has no equivalent of Fossify's "isSystemThemeEnabled"
// config flag to gate dynamic color, so instead of disabling Material You at
// runtime, the dynamic scheme calls (dynamicDarkColorScheme /
// dynamicLightColorScheme) are removed entirely and replaced with this fixed
// scheme, applied regardless of system light/dark setting to match the rest
// of the suite always rendering dark.
private val WaxdBackground = Color(0xFF0A0C0A)
private val WaxdText = Color(0xFFD8F5E0)
private val WaxdPrimary = Color(0xFF2E9E5B)
private val WaxdAccent = Color(0xFF4CFF8B)

private val WaxdColorScheme = darkColorScheme(
    primary = WaxdPrimary,
    onPrimary = WaxdBackground,
    secondary = WaxdAccent,
    onSecondary = WaxdBackground,
    background = WaxdBackground,
    onBackground = WaxdText,
    surface = WaxdBackground,
    onSurface = WaxdText,
)

@Composable
fun PdfViewerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = WaxdColorScheme, content = content)
}

@Composable
fun darkTopAppBarColors(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = WaxdColorScheme.surface,
        titleContentColor = WaxdColorScheme.onSurface,
        actionIconContentColor = WaxdColorScheme.onSurfaceVariant
    )
}

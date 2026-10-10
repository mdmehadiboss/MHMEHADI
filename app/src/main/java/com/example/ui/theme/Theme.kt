package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GlassDarkColorScheme = darkColorScheme(
    primary = EmeraldNeon,
    onPrimary = Color(0xFF0A0A0B),
    primaryContainer = Color(0x3310B981),
    onPrimaryContainer = EmeraldLight,
    secondary = CyanNeon,
    onSecondary = Color(0xFF0A0A0B),
    secondaryContainer = Color(0x333B82F6),
    onSecondaryContainer = CyanLight,
    tertiary = IndigoNeon,
    onTertiary = Color.White,
    background = DeepSpace,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    error = RoseNeon,
    onError = Color.White
)

@Composable
fun GlassPosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GlassDarkColorScheme,
        typography = Typography,
        content = content
    )
}

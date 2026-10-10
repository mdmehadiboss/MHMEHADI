package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Elegant Dark Canvas & Tones
val DeepSpace = Color(0xFF0A0A0B)
val DarkNavy = Color(0xFF0F1115)
val SurfaceDark = Color(0xFF121418)
val SurfaceCard = Color(0xFF16181D)
val SurfaceCardTransparent = Color(0x9916181D)
val GlassBackground = Color(0x0DFFFFFF) // bg-white/5
val GlassBorder = Color(0x1AFFFFFF) // border-white/10
val GlassBorderFocused = Color(0x8010B981)

// Neon & Brand Accent Colors
val EmeraldNeon = Color(0xFF10B981) // emerald-500
val EmeraldLight = Color(0xFF34D399) // emerald-400
val EmeraldDark = Color(0xFF059669) // emerald-600

val CyanNeon = Color(0xFF3B82F6) // blue-500
val CyanLight = Color(0xFF60A5FA) // blue-400
val CyanDark = Color(0xFF2563EB) // blue-600

val IndigoNeon = Color(0xFF6366F1) // indigo-500
val PurpleNeon = Color(0xFF8B5CF6) // purple-500
val AmberNeon = Color(0xFFF59E0B) // amber-500
val RoseNeon = Color(0xFFF43F5E) // rose-500

// Text Tones (Slate palette)
val TextPrimary = Color(0xFFF8FAFC) // slate-100
val TextSecondary = Color(0xFF94A3B8) // slate-400
val TextMuted = Color(0xFF64748B) // slate-500

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    listOf(EmeraldNeon, Color(0xFF059669))
)

val AccentGradient = Brush.horizontalGradient(
    listOf(CyanNeon, IndigoNeon)
)

val AmberGradient = Brush.horizontalGradient(
    listOf(AmberNeon, Color(0xFFEA580C))
)

val GlassCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x1AFFFFFF),
        Color(0x08FFFFFF)
    )
)

val BackgroundMeshGradient = Brush.verticalGradient(
    listOf(
        DeepSpace,
        Color(0xFF0E1013),
        DeepSpace
    )
)


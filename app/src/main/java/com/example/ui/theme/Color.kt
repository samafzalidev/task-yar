package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Frosted Glass Premium Dark palette
val FrostedDarkBg = Color(0xFF0F1115)         // Deep charcoal/slate
val FrostedDarkSurface = Color(0x0EFFFFFF)    // White 5.5% translucent for standard glass cards
val FrostedDarkSurfaceVariant = Color(0x1CFFFFFF) // White 11% translucent for active/hovered state
val FrostedDarkBorder = Color(0x1DFFFFFF)     // White 11.5% translucent fine border
val FrostedPrimary = Color(0xFF3B82F6)        // Frosted Blue accent
val FrostedPrimaryGlow = Color(0x333B82F6)   // Frosted Blue 20% glow
val FrostedSecondary = Color(0xFF10B981)      // Success Green/Emerald
val FrostedTertiary = Color(0xFF8B5CF6)       // Purple Glow accent

// Keep compatibility with files importing CosmicPrimary
val CosmicPrimary = FrostedPrimary
val CosmicSecondary = FrostedSecondary
val CosmicTertiary = FrostedTertiary
val CosmicDarkBg = FrostedDarkBg
val CosmicDarkSurface = Color(0xFF171A21)
val CosmicDarkBorder = FrostedDarkBorder

// Text hierarchy colors to preserve contrast and premium readability
val FrostedTextPrimary = Color(0xFFE2E2E6)
val FrostedTextMuted = Color(0xFF919194)

// Frosted Glass Light palette values (subtle glass sheet on ivory-slate backing)
val FrostedLightBg = Color(0xFFECEFF1)
val FrostedLightSurface = Color(0x99FFFFFF) // 60% transparent white
val FrostedLightSurfaceVariant = Color(0xCCFFFFFF) // 80% transparent white
val FrostedLightBorder = Color(0x40FFFFFF) // Semi-transparent stark white outline

val CosmicLightBg = FrostedLightBg
val CosmicLightSurface = Color(0xFFF8FAFC)
val CosmicLightBorder = Color(0xFFE2E8F0)

// Status and Priority accents
val PriorityLow = Color(0xFF10B981)       // Emerald Green
val PriorityMedium = Color(0xFFF59E0B)    // Amber Orange
val PriorityHigh = Color(0xFFEF4444)      // Rose Red
val PriorityLowBg = Color(0x2610B981)
val PriorityMediumBg = Color(0x26F59E0B)
val PriorityHighBg = Color(0x26EF4444)

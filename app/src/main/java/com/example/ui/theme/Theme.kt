package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClearPathColorScheme = lightColorScheme(
  primary = DarkForestSage,
  onPrimary = CreamWhite,
  primaryContainer = SoftSageLight,
  onPrimaryContainer = DeepCharcoal,
  secondary = MutedBlue,
  onSecondary = CreamWhite,
  secondaryContainer = MutedBlueLight,
  onSecondaryContainer = DeepCharcoal,
  tertiary = SoftClay,
  onTertiary = CreamWhite,
  tertiaryContainer = SoftClayLight,
  onTertiaryContainer = DeepCharcoal,
  background = WarmIvory,
  onBackground = DeepCharcoal,
  surface = CreamWhite,
  onSurface = DeepCharcoal,
  surfaceVariant = WarmSandLight,
  onSurfaceVariant = DeepCharcoal,
  outline = CharcoalBorder,
  surfaceContainer = CreamWhite,
  surfaceContainerHigh = CreamWhite,
  surfaceContainerHighest = WarmSandLight,
  surfaceContainerLow = WarmIvory,
  surfaceContainerLowest = CreamWhite
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Always preserve ClearPath's intentional warm calming palette
  content: @Composable () -> Unit,
) {
  // Use ClearPath's unified warm paper palette ensuring maximum text visibility and zero contrast mismatch
  MaterialTheme(
    colorScheme = ClearPathColorScheme,
    typography = Typography,
    content = content
  )
}


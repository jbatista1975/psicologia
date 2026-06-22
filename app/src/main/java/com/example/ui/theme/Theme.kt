package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = AlexandriaDarkPrimary,
    primaryContainer = AlexandriaDarkPrimaryContainer,
    secondary = AlexandriaDarkSecondary,
    secondaryContainer = AlexandriaDarkSecondaryContainer,
    tertiary = AlexandriaDarkTertiary,
    background = AlexandriaDarkBackground,
    surface = AlexandriaDarkSurface,
    onPrimary = AlexandriaDarkOnPrimary,
    onBackground = AlexandriaDarkOnBackground,
    onSurface = AlexandriaDarkOnSurface
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AlexandriaPrimary,
    primaryContainer = AlexandriaPrimaryContainer,
    secondary = AlexandriaSecondary,
    secondaryContainer = AlexandriaSecondaryContainer,
    tertiary = AlexandriaTertiary,
    background = AlexandriaBackground,
    surface = AlexandriaSurface,
    onPrimary = AlexandriaOnPrimary,
    onBackground = AlexandriaOnBackground,
    onSurface = AlexandriaOnSurface,
    onSurfaceVariant = AlexandriaOnSurfaceVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

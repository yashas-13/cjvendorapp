package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ForgeAmberLight,
    onPrimary = Color.Black,
    primaryContainer = ForgeAmberContainer,
    onPrimaryContainer = Color(0xFFFFDDB3),
    secondary = SteelSlateLight,
    onSecondary = Color.Black,
    secondaryContainer = SteelSlateDark,
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = LaserCyanLight,
    onTertiary = Color.Black,
    tertiaryContainer = LaserCyanContainer,
    onTertiaryContainer = Color(0xFFBAE6FD),
    background = IndustrialDarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = IndustrialSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = IndustrialSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    surfaceContainer = IndustrialSurfaceContainer,
    outline = SteelSlate
)

private val LightColorScheme = lightColorScheme(
    primary = ForgeAmberPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = SteelSlate,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = LaserCyan,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF0369A1),
    background = SteelLightBg,
    onBackground = Color(0xFF0F172A),
    surface = SteelLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = SteelLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF94A3B8)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set default to false to retain consistent industrial brand look
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

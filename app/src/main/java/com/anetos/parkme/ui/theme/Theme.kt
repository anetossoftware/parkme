package com.anetos.parkme.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.anetos.parkme.core.helper.Theme

@Composable
fun AppTheme(
    theme: Theme, content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalDimensions provides dimensions) {
        with(theme) {
            MaterialTheme(colorScheme, shapes, typography, content)
        }
    }
}

object AppTheme {
    val dimensions: Dimensions
        @Composable
        get() = LocalDimensions.current
}

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    secondary = appSecondaryColor,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = appSurfaceColor,
    onSurface = Color.Black,
    surfaceVariant = appSurfaceColor,
    onSurfaceVariant = Color.Black,
    outline = appSecondaryColor,
    /*primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40*/

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color(0xFFBDBDBD),
    onSecondary = Color.Black,
    background = appPrimaryColor,
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color.White,
    outline = Color(0xFFBDBDBD),
    /*primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80*/
)

private val Theme.colorScheme: ColorScheme
    @Composable
    get() {
        val isDarkMode = isSystemInDarkTheme()
        return when (this) {
            Theme.System -> if (isDarkMode) DarkColorScheme else LightColorScheme
            Theme.Light -> LightColorScheme
            Theme.Dark -> DarkColorScheme
        }
    }

private val dimensions = Dimensions()

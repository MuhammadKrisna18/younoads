package com.example.yoouutt.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

private val CyberpunkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = PureBlack,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = ElectricCyan,
    secondary = NeonMagenta,
    onSecondary = PureWhite,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = NeonMagenta,
    tertiary = CyberViolet,
    background = DarkBackground,
    onBackground = PureWhite,
    surface = DarkSurface,
    onSurface = PureWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

private val MatteBlackColorScheme = darkColorScheme(
    primary = OfficialYouTubeRed,
    onPrimary = PureWhite,
    primaryContainer = MatteBlackSurfaceVariant,
    onPrimaryContainer = OfficialYouTubeRed,
    secondary = OfficialYouTubeRed,
    onSecondary = PureWhite,
    secondaryContainer = MatteBlackSurfaceVariant,
    onSecondaryContainer = PureWhite,
    tertiary = Color(0xFFCC0000),
    background = MatteBlackBackground,
    onBackground = PureWhite,
    surface = MatteBlackSurface,
    onSurface = PureWhite,
    surfaceVariant = MatteBlackSurfaceVariant,
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = MatteBlackOutline
)

private val CleanLightColorScheme = lightColorScheme(
    primary = Color(0xFFFF0000),
    onPrimary = PureWhite,
    primaryContainer = LightPureSurfaceVariant,
    onPrimaryContainer = Color(0xFFFF0000),
    secondary = Color(0xFF065FD4),
    onSecondary = PureWhite,
    secondaryContainer = LightPureSurfaceVariant,
    onSecondaryContainer = Color(0xFF065FD4),
    tertiary = Color(0xFF065FD4),
    background = LightPureBackground,
    onBackground = LightTextPrimary,
    surface = LightPureSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightPureSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightPureOutline
)

private val SakuraLofiColorScheme = darkColorScheme(
    primary = SakuraPink,
    onPrimary = PureBlack,
    primaryContainer = SakuraSurfaceVariant,
    onPrimaryContainer = SakuraPink,
    secondary = PastelLavender,
    onSecondary = PureBlack,
    secondaryContainer = SakuraSurfaceVariant,
    onSecondaryContainer = PastelLavender,
    tertiary = PastelLavender,
    background = SakuraBackground,
    onBackground = PureWhite,
    surface = SakuraSurface,
    onSurface = PureWhite,
    surfaceVariant = SakuraSurfaceVariant,
    onSurfaceVariant = Color(0xFFD4C2DC),
    outline = SakuraOutline
)

private val CityPopSunsetColorScheme = darkColorScheme(
    primary = SunsetOrange,
    onPrimary = PureBlack,
    primaryContainer = CityPopSurfaceVariant,
    onPrimaryContainer = SunsetOrange,
    secondary = SunsetAmber,
    onSecondary = PureBlack,
    secondaryContainer = CityPopSurfaceVariant,
    onSecondaryContainer = SunsetAmber,
    tertiary = SunsetAmber,
    background = CityPopBackground,
    onBackground = PureWhite,
    surface = CityPopSurface,
    onSurface = PureWhite,
    surfaceVariant = CityPopSurfaceVariant,
    onSurfaceVariant = Color(0xFFD0D7E5),
    outline = CityPopOutline
)

private val MechaTacticalColorScheme = darkColorScheme(
    primary = EvaNeonGreen,
    onPrimary = PureBlack,
    primaryContainer = MechaSurfaceVariant,
    onPrimaryContainer = EvaNeonGreen,
    secondary = TacticalAmber,
    onSecondary = PureBlack,
    secondaryContainer = MechaSurfaceVariant,
    onSecondaryContainer = TacticalAmber,
    tertiary = TacticalAmber,
    background = MechaBackground,
    onBackground = PureWhite,
    surface = MechaSurface,
    onSurface = PureWhite,
    surfaceVariant = MechaSurfaceVariant,
    onSurfaceVariant = Color(0xFFB0BDC9),
    outline = MechaOutline
)

val LocalYoouuttGradient = staticCompositionLocalOf { CyberpunkGradient }
val LocalYoouuttTheme = staticCompositionLocalOf { AppTheme.CYBERPUNK }

fun AppTheme.gradientColors(): List<Color> = when (this) {
    AppTheme.CYBERPUNK -> listOf(ElectricCyan, NeonMagenta)
    AppTheme.MATTE_BLACK -> listOf(OfficialYouTubeRed, Color(0xFFCC0000))
    AppTheme.LIGHT -> listOf(Color(0xFFFF0000), Color(0xFF065FD4))
    AppTheme.SAKURA_LOFI -> listOf(SakuraPink, PastelLavender)
    AppTheme.CITY_POP_SUNSET -> listOf(SunsetOrange, SunsetAmber)
    AppTheme.MECHA_TACTICAL -> listOf(EvaNeonGreen, TacticalAmber)
}

fun AppTheme.gradient(): Brush = Brush.horizontalGradient(gradientColors())

fun AppTheme.colorScheme(): ColorScheme = when (this) {
    AppTheme.CYBERPUNK -> CyberpunkColorScheme
    AppTheme.MATTE_BLACK -> MatteBlackColorScheme
    AppTheme.LIGHT -> CleanLightColorScheme
    AppTheme.SAKURA_LOFI -> SakuraLofiColorScheme
    AppTheme.CITY_POP_SUNSET -> CityPopSunsetColorScheme
    AppTheme.MECHA_TACTICAL -> MechaTacticalColorScheme
}

@Composable
fun YoouuttTheme(
    appTheme: AppTheme = AppTheme.CYBERPUNK,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalYoouuttTheme provides appTheme,
        LocalYoouuttGradient provides appTheme.gradient()
    ) {
        MaterialTheme(
            colorScheme = appTheme.colorScheme(),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

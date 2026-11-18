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

private val NatsuUmiColorScheme = lightColorScheme(
    primary = NatsuPrimary,
    onPrimary = PureWhite,
    primaryContainer = NatsuSurfaceVariant,
    onPrimaryContainer = NatsuPrimary,
    secondary = NatsuSecondary,
    onSecondary = PureBlack,
    secondaryContainer = NatsuSurfaceVariant,
    onSecondaryContainer = NatsuTextPrimary,
    tertiary = NatsuSecondary,
    background = NatsuBackground,
    onBackground = NatsuTextPrimary,
    surface = NatsuSurface,
    onSurface = NatsuTextPrimary,
    surfaceVariant = NatsuSurfaceVariant,
    onSurfaceVariant = NatsuTextSecondary,
    outline = NatsuOutline
)

private val MomijiAutumnColorScheme = darkColorScheme(
    primary = MomijiPrimary,
    onPrimary = PureWhite,
    primaryContainer = MomijiSurfaceVariant,
    onPrimaryContainer = MomijiPrimary,
    secondary = MomijiSecondary,
    onSecondary = PureBlack,
    secondaryContainer = MomijiSurfaceVariant,
    onSecondaryContainer = MomijiSecondary,
    tertiary = MomijiTertiary,
    background = MomijiBackground,
    onBackground = MomijiForeground,
    surface = MomijiSurface,
    onSurface = MomijiForeground,
    surfaceVariant = MomijiSurfaceVariant,
    onSurfaceVariant = MomijiOnSurfaceVariant,
    outline = MomijiOutline
)

private val FuyuYukiColorScheme = lightColorScheme(
    primary = FuyuPrimary,
    onPrimary = PureWhite,
    primaryContainer = FuyuSurfaceVariant,
    onPrimaryContainer = FuyuPrimary,
    secondary = FuyuSecondary,
    onSecondary = PureWhite,
    secondaryContainer = FuyuSurfaceVariant,
    onSecondaryContainer = FuyuSecondary,
    tertiary = FuyuSecondary,
    background = FuyuBackground,
    onBackground = FuyuTextPrimary,
    surface = FuyuSurface,
    onSurface = FuyuTextPrimary,
    surfaceVariant = FuyuSurfaceVariant,
    onSurfaceVariant = FuyuTextSecondary,
    outline = FuyuOutline
)

private val MatchaZenColorScheme = lightColorScheme(
    primary = MatchaPrimary,
    onPrimary = PureWhite,
    primaryContainer = MatchaSurfaceVariant,
    onPrimaryContainer = MatchaPrimary,
    secondary = MatchaSecondary,
    onSecondary = PureBlack,
    secondaryContainer = MatchaSurfaceVariant,
    onSecondaryContainer = MatchaTextPrimary,
    tertiary = MatchaSecondary,
    background = MatchaBackground,
    onBackground = MatchaTextPrimary,
    surface = MatchaSurface,
    onSurface = MatchaTextPrimary,
    surfaceVariant = MatchaSurfaceVariant,
    onSurfaceVariant = MatchaTextSecondary,
    outline = MatchaOutline
)

private val OceanAbyssColorScheme = darkColorScheme(
    primary = AbyssPrimary,
    onPrimary = PureBlack,
    primaryContainer = AbyssSurfaceVariant,
    onPrimaryContainer = AbyssPrimary,
    secondary = AbyssSecondary,
    onSecondary = PureBlack,
    secondaryContainer = AbyssSurfaceVariant,
    onSecondaryContainer = AbyssSecondary,
    tertiary = AbyssSecondary,
    background = AbyssBackground,
    onBackground = AbyssForeground,
    surface = AbyssSurface,
    onSurface = AbyssForeground,
    surfaceVariant = AbyssSurfaceVariant,
    onSurfaceVariant = AbyssOnSurfaceVariant,
    outline = AbyssOutline
)

private val SumieInkColorScheme = darkColorScheme(
    primary = SumiePrimary,
    onPrimary = PureWhite,
    primaryContainer = SumieSurfaceVariant,
    onPrimaryContainer = SumiePrimary,
    secondary = SumieSecondary,
    onSecondary = PureBlack,
    secondaryContainer = SumieSurfaceVariant,
    onSecondaryContainer = SumieSecondary,
    tertiary = SumiePrimary,
    background = SumieBackground,
    onBackground = SumieForeground,
    surface = SumieSurface,
    onSurface = SumieForeground,
    surfaceVariant = SumieSurfaceVariant,
    onSurfaceVariant = SumieOnSurfaceVariant,
    outline = SumieOutline
)

private val DraculaColorScheme = darkColorScheme(
    primary = DraculaPink,
    onPrimary = PureBlack,
    primaryContainer = DraculaSurfaceVariant,
    onPrimaryContainer = DraculaPink,
    secondary = DraculaCyan,
    onSecondary = PureBlack,
    secondaryContainer = DraculaSurfaceVariant,
    onSecondaryContainer = DraculaCyan,
    tertiary = DraculaGreen,
    background = DraculaBackground,
    onBackground = DraculaForeground,
    surface = DraculaSurface,
    onSurface = DraculaForeground,
    surfaceVariant = DraculaSurfaceVariant,
    onSurfaceVariant = DraculaOnSurfaceVariant,
    outline = DraculaOutline
)

private val NordColorScheme = darkColorScheme(
    primary = NordFrostTeal,
    onPrimary = PureBlack,
    primaryContainer = NordSurfaceVariant,
    onPrimaryContainer = NordFrostTeal,
    secondary = NordFrostBlue,
    onSecondary = PureBlack,
    secondaryContainer = NordSurfaceVariant,
    onSecondaryContainer = NordFrostBlue,
    tertiary = NordAuroraYellow,
    background = NordBackground,
    onBackground = NordForeground,
    surface = NordSurface,
    onSurface = NordForeground,
    surfaceVariant = NordSurfaceVariant,
    onSurfaceVariant = NordOnSurfaceVariant,
    outline = NordOutline
)

private val CatppuccinMochaColorScheme = darkColorScheme(
    primary = MochaMauve,
    onPrimary = PureBlack,
    primaryContainer = MochaSurfaceVariant,
    onPrimaryContainer = MochaMauve,
    secondary = MochaSapphire,
    onSecondary = PureBlack,
    secondaryContainer = MochaSurfaceVariant,
    onSecondaryContainer = MochaSapphire,
    tertiary = MochaPeach,
    background = MochaBackground,
    onBackground = MochaForeground,
    surface = MochaSurface,
    onSurface = MochaForeground,
    surfaceVariant = MochaSurfaceVariant,
    onSurfaceVariant = MochaOnSurfaceVariant,
    outline = MochaOutline
)

private val RetroPixelColorScheme = darkColorScheme(
    primary = RetroPixelBright,
    onPrimary = RetroPixelBackground,
    primaryContainer = RetroPixelSurfaceVariant,
    onPrimaryContainer = RetroPixelBright,
    secondary = RetroPixelMid,
    onSecondary = RetroPixelBackground,
    secondaryContainer = RetroPixelSurfaceVariant,
    onSecondaryContainer = RetroPixelMid,
    tertiary = RetroPixelMid,
    background = RetroPixelBackground,
    onBackground = RetroPixelBright,
    surface = RetroPixelSurface,
    onSurface = RetroPixelBright,
    surfaceVariant = RetroPixelSurfaceVariant,
    onSurfaceVariant = RetroPixelMid,
    outline = RetroPixelOutline
)

private val TerminalHackerColorScheme = darkColorScheme(
    primary = TerminalGreen,
    onPrimary = PureBlack,
    primaryContainer = TerminalSurfaceVariant,
    onPrimaryContainer = TerminalGreen,
    secondary = TerminalLime,
    onSecondary = PureBlack,
    secondaryContainer = TerminalSurfaceVariant,
    onSecondaryContainer = TerminalLime,
    tertiary = TerminalAmber,
    background = TerminalBackground,
    onBackground = TerminalGreen,
    surface = TerminalSurface,
    onSurface = TerminalGreen,
    surfaceVariant = TerminalSurfaceVariant,
    onSurfaceVariant = Color(0xFF00CC52),
    outline = TerminalOutline
)

private val VaporwaveColorScheme = darkColorScheme(
    primary = VaporwaveHotPink,
    onPrimary = PureBlack,
    primaryContainer = VaporwaveSurfaceVariant,
    onPrimaryContainer = VaporwaveHotPink,
    secondary = VaporwaveCyan,
    onSecondary = PureBlack,
    secondaryContainer = VaporwaveSurfaceVariant,
    onSecondaryContainer = VaporwaveCyan,
    tertiary = VaporwaveYellow,
    background = VaporwaveBackground,
    onBackground = VaporwaveForeground,
    surface = VaporwaveSurface,
    onSurface = VaporwaveForeground,
    surfaceVariant = VaporwaveSurfaceVariant,
    onSurfaceVariant = VaporwaveOnSurfaceVariant,
    outline = VaporwaveOutline
)

private val SepiaMalamColorScheme = darkColorScheme(
    primary = SepiaAmber,
    onPrimary = PureBlack,
    primaryContainer = SepiaSurfaceVariant,
    onPrimaryContainer = SepiaAmber,
    secondary = SepiaGold,
    onSecondary = PureBlack,
    secondaryContainer = SepiaSurfaceVariant,
    onSecondaryContainer = SepiaGold,
    tertiary = SepiaGold,
    background = SepiaBackground,
    onBackground = SepiaForeground,
    surface = SepiaSurface,
    onSurface = SepiaForeground,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = SepiaOnSurfaceVariant,
    outline = SepiaOutline
)

private val KontrasTinggiColorScheme = darkColorScheme(
    primary = HighContrastYellow,
    onPrimary = PureBlack,
    primaryContainer = HighContrastSurfaceVariant,
    onPrimaryContainer = HighContrastYellow,
    secondary = HighContrastWhite,
    onSecondary = PureBlack,
    secondaryContainer = HighContrastSurfaceVariant,
    onSecondaryContainer = HighContrastWhite,
    tertiary = HighContrastYellow,
    background = HighContrastBackground,
    onBackground = HighContrastForeground,
    surface = HighContrastSurface,
    onSurface = HighContrastForeground,
    surfaceVariant = HighContrastSurfaceVariant,
    onSurfaceVariant = HighContrastOnSurfaceVariant,
    outline = HighContrastOutline
)

private val AmoledRedupColorScheme = darkColorScheme(
    primary = AmoledRedupCrimson,
    onPrimary = PureBlack,
    primaryContainer = AmoledRedupSurfaceVariant,
    onPrimaryContainer = AmoledRedupCrimson,
    secondary = AmoledRedupBurgundy,
    onSecondary = PureWhite,
    secondaryContainer = AmoledRedupSurfaceVariant,
    onSecondaryContainer = AmoledRedupBurgundy,
    tertiary = AmoledRedupBurgundy,
    background = AmoledRedupBackground,
    onBackground = AmoledRedupForeground,
    surface = AmoledRedupSurface,
    onSurface = AmoledRedupForeground,
    surfaceVariant = AmoledRedupSurfaceVariant,
    onSurfaceVariant = AmoledRedupOnSurfaceVariant,
    outline = AmoledRedupOutline
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
    AppTheme.NATSU_UMI -> listOf(NatsuPrimary, NatsuSecondary)
    AppTheme.MOMIJI_AUTUMN -> listOf(MomijiPrimary, MomijiSecondary)
    AppTheme.FUYU_YUKI -> listOf(FuyuPrimary, FuyuSecondary)
    AppTheme.MATCHA_ZEN -> listOf(MatchaPrimary, MatchaSecondary)
    AppTheme.OCEAN_ABYSS -> listOf(AbyssPrimary, AbyssSecondary)
    AppTheme.SUMIE_INK -> listOf(SumiePrimary, SumieSecondary)
    AppTheme.DRACULA -> listOf(DraculaPink, DraculaPurple)
    AppTheme.NORD -> listOf(NordFrostTeal, NordFrostBlue)
    AppTheme.CATPPUCCIN_MOCHA -> listOf(MochaMauve, MochaPink)
    AppTheme.RETRO_PIXEL -> listOf(RetroPixelBright, RetroPixelMid)
    AppTheme.TERMINAL_HACKER -> listOf(TerminalGreen, TerminalLime)
    AppTheme.VAPORWAVE -> listOf(VaporwaveHotPink, VaporwaveCyan)
    AppTheme.SEPIA_MALAM -> listOf(SepiaAmber, SepiaGold)
    AppTheme.KONTRAS_TINGGI -> listOf(HighContrastYellow, HighContrastWhite)
    AppTheme.AMOLED_REDUP -> listOf(AmoledRedupCrimson, Color(0xFF8E0000))
}

fun AppTheme.previewColors(): List<Color> = when (this) {
    AppTheme.CYBERPUNK -> listOf(ElectricCyan, NeonMagenta, DarkBackground)
    AppTheme.MATTE_BLACK -> listOf(OfficialYouTubeRed, MatteBlackSurfaceVariant, MatteBlackBackground)
    AppTheme.LIGHT -> listOf(Color(0xFFFF0000), Color(0xFF065FD4), LightPureBackground)
    AppTheme.SAKURA_LOFI -> listOf(SakuraPink, PastelLavender, SakuraBackground)
    AppTheme.CITY_POP_SUNSET -> listOf(SunsetOrange, SunsetAmber, CityPopBackground)
    AppTheme.MECHA_TACTICAL -> listOf(EvaNeonGreen, TacticalAmber, MechaBackground)
    AppTheme.NATSU_UMI -> listOf(NatsuPrimary, NatsuSecondary, NatsuBackground)
    AppTheme.MOMIJI_AUTUMN -> listOf(MomijiPrimary, MomijiSecondary, MomijiBackground)
    AppTheme.FUYU_YUKI -> listOf(FuyuPrimary, FuyuSecondary, FuyuBackground)
    AppTheme.MATCHA_ZEN -> listOf(MatchaPrimary, MatchaSecondary, MatchaBackground)
    AppTheme.OCEAN_ABYSS -> listOf(AbyssPrimary, AbyssSecondary, AbyssBackground)
    AppTheme.SUMIE_INK -> listOf(SumiePrimary, SumieSecondary, SumieBackground)
    AppTheme.DRACULA -> listOf(DraculaPink, DraculaCyan, DraculaBackground)
    AppTheme.NORD -> listOf(NordFrostTeal, NordFrostBlue, NordBackground)
    AppTheme.CATPPUCCIN_MOCHA -> listOf(MochaMauve, MochaSapphire, MochaBackground)
    AppTheme.RETRO_PIXEL -> listOf(RetroPixelBright, RetroPixelOutline, RetroPixelBackground)
    AppTheme.TERMINAL_HACKER -> listOf(TerminalGreen, TerminalSurfaceVariant, TerminalBackground)
    AppTheme.VAPORWAVE -> listOf(VaporwaveHotPink, VaporwaveCyan, VaporwaveBackground)
    AppTheme.SEPIA_MALAM -> listOf(SepiaAmber, SepiaGold, SepiaBackground)
    AppTheme.KONTRAS_TINGGI -> listOf(HighContrastYellow, HighContrastWhite, HighContrastBackground)
    AppTheme.AMOLED_REDUP -> listOf(AmoledRedupCrimson, AmoledRedupSurfaceVariant, AmoledRedupBackground)
}

fun AppTheme.gradient(): Brush = Brush.horizontalGradient(gradientColors())

fun AppTheme.colorScheme(): ColorScheme = when (this) {
    AppTheme.CYBERPUNK -> CyberpunkColorScheme
    AppTheme.MATTE_BLACK -> MatteBlackColorScheme
    AppTheme.LIGHT -> CleanLightColorScheme
    AppTheme.SAKURA_LOFI -> SakuraLofiColorScheme
    AppTheme.CITY_POP_SUNSET -> CityPopSunsetColorScheme
    AppTheme.MECHA_TACTICAL -> MechaTacticalColorScheme
    AppTheme.NATSU_UMI -> NatsuUmiColorScheme
    AppTheme.MOMIJI_AUTUMN -> MomijiAutumnColorScheme
    AppTheme.FUYU_YUKI -> FuyuYukiColorScheme
    AppTheme.MATCHA_ZEN -> MatchaZenColorScheme
    AppTheme.OCEAN_ABYSS -> OceanAbyssColorScheme
    AppTheme.SUMIE_INK -> SumieInkColorScheme
    AppTheme.DRACULA -> DraculaColorScheme
    AppTheme.NORD -> NordColorScheme
    AppTheme.CATPPUCCIN_MOCHA -> CatppuccinMochaColorScheme
    AppTheme.RETRO_PIXEL -> RetroPixelColorScheme
    AppTheme.TERMINAL_HACKER -> TerminalHackerColorScheme
    AppTheme.VAPORWAVE -> VaporwaveColorScheme
    AppTheme.SEPIA_MALAM -> SepiaMalamColorScheme
    AppTheme.KONTRAS_TINGGI -> KontrasTinggiColorScheme
    AppTheme.AMOLED_REDUP -> AmoledRedupColorScheme
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

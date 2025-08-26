package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ColorSchemeState2")
data class ColorScheme2(
    val primary: Long,
    val onPrimary: Long,
    val primaryContainer: Long,
    val onPrimaryContainer: Long,
    val inversePrimary: Long,
    val secondary: Long,
    val onSecondary: Long,
    val secondaryContainer: Long,
    val onSecondaryContainer: Long,
    val tertiary: Long,
    val onTertiary: Long,
    val tertiaryContainer: Long,
    val onTertiaryContainer: Long,
    val background: Long,
    val onBackground: Long,
    val surface: Long,
    val onSurface: Long,
    val surfaceVariant: Long,
    val onSurfaceVariant: Long,
    val surfaceTint: Long,
    val inverseSurface: Long,
    val inverseOnSurface: Long,
    val error: Long,
    val onError: Long,
    val errorContainer: Long,
    val onErrorContainer: Long,
    val outline: Long,
    val outlineVariant: Long,
    val scrim: Long,
    val surfaceBright: Long,
    val surfaceDim: Long,
    val surfaceContainer: Long,
    val surfaceContainerHigh: Long,
    val surfaceContainerHighest: Long,
    val surfaceContainerLow: Long,
    val surfaceContainerLowest: Long,
) {
    @Serializable
    sealed class Color2 {
        @Serializable
        data object Primary : Color2()
        @Serializable
        data object OnPrimary : Color2()
        @Serializable
        data object PrimaryContainer : Color2()
        @Serializable
        data object OnPrimaryContainer : Color2()
        @Serializable
        data object InversePrimary : Color2()

        @Serializable
        data object Secondary : Color2()
        @Serializable
        data object OnSecondary : Color2()
        @Serializable
        data object SecondaryContainer : Color2()
        @Serializable
        data object OnSecondaryContainer : Color2()

        @Serializable
        data object Tertiary : Color2()
        @Serializable
        data object OnTertiary : Color2()
        @Serializable
        data object TertiaryContainer : Color2()
        @Serializable
        data object OnTertiaryContainer : Color2()

        @Serializable
        data object Background : Color2()
        @Serializable
        data object OnBackground : Color2()

        @Serializable
        data object Surface : Color2()
        @Serializable
        data object SurfaceVariant : Color2()
        @Serializable
        data object OnSurface : Color2()
        @Serializable
        data object OnSurfaceVariant : Color2()
        @Serializable
        data object SurfaceTint : Color2()
        @Serializable
        data object InverseSurface : Color2()
        @Serializable
        data object InverseOnSurface : Color2()

        @Serializable
        data object Error : Color2()
        @Serializable
        data object OnError : Color2()
        @Serializable
        data object ErrorContainer : Color2()
        @Serializable
        data object OnErrorContainer : Color2()

        @Serializable
        data object Outline : Color2()
        @Serializable
        data object OutlineVariant : Color2()

        @Serializable
        data object Scrim : Color2()
        @Serializable
        data object SurfaceDim : Color2()
        @Serializable
        data object SurfaceBright : Color2()
        @Serializable
        data object SurfaceContainer : Color2()
        @Serializable
        data object SurfaceContainerHigh : Color2()
        @Serializable
        data object SurfaceContainerHighest : Color2()
        @Serializable
        data object SurfaceContainerLow : Color2()
        @Serializable
        data object SurfaceContainerLowest : Color2()

        @Serializable
        data object Unspecified : Color2()
    }
    
    companion object {
        val DefaultLightColorScheme2 = ColorScheme2(
            primary = 0xFFE64A19,
            onPrimary = 0xFFFFFFFF,
            secondary = 0xFF717680,
            onSecondary = 0xFFFFFFFF,
            onSecondaryContainer = 0xFF414651,
            secondaryContainer = 0xFFFFFFFF,
            background = 0xFFF9F9F9,
            onBackground = 0xFF1C1C1E,
            surface = 0xFFFFFFFF,
            onSurface = 0xFF1C1C1E,
            surfaceVariant = 0xFFF1F1F1,
            onSurfaceVariant = 0xFF6E6E6E,
            outline = 0xFFBDBDBD,
            outlineVariant = 0xFFBDBDBD,
            error = 0xFFFF0000,
            onError = 0xFFFFFFFF,
            primaryContainer = 0xFFFF00FF, // Not yet implemented,
            onPrimaryContainer = 0xFFFF00FF, // Not yet implemented,
            inversePrimary = 0xFFFF00FF, // Not yet implemented,
            tertiary = 0xFFFF00FF, // Not yet implemented,
            onTertiary = 0xFFFF00FF, // Not yet implemented,
            tertiaryContainer = 0xFFFF00FF, // Not yet implemented,
            onTertiaryContainer = 0xFFFF00FF, // Not yet implemented,
            surfaceTint = 0xFFFF00FF, // Not yet implemented,
            inverseSurface = 0xFF000000,
            inverseOnSurface = 0xFFFF00FF, // Not yet implemented,
            errorContainer = 0xFFFF00FF, // Not yet implemented,
            onErrorContainer = 0xFFFF00FF, // Not yet implemented,
            scrim = 0xFFFF00FF, // Not yet implemented,
            surfaceBright = 0xFFFF00FF, // Not yet implemented,
            surfaceDim = 0xFFFF00FF, // Not yet implemented,
            surfaceContainer = 0xFFFF00FF, // Not yet implemented,
            surfaceContainerHigh = 0xFFFF00FF, // Not yet implemented,
            surfaceContainerHighest = 0xFFFF00FF, // Not yet implemented,
            surfaceContainerLow = 0xFFFF00FF, // Not yet implemented,
            surfaceContainerLowest = 0xFFFF00FF, // Not yet implemented,
        )
        
        val DefaultDarkColorScheme2 = ColorScheme2(
            primary = 0xFFE64A19,
            onPrimary = 0xFFFFFFFF,
            secondary = 0xFF717680,
            onSecondary = 0xFFFFFFFF,
            onSecondaryContainer = 0xFF414651,
            secondaryContainer = 0xFFFFFFFF,
            background = 0xFF1E1E1E,
            onBackground = 0xFFF1F1F1,
            surface = 0xFF121212,
            onSurface = 0xFFF1F1F1,
            surfaceVariant = 0xFF2A2A2A,
            onSurfaceVariant = 0xFFB0B0B0,
            outline = 0xFF444444,
            outlineVariant = 0xFFBDBDBD,
            error = 0xFFFF0000,
            onError = 0xFFFFFFFF,
            primaryContainer = 0xFFFF00FF, // Not yet implemented,
            onPrimaryContainer = 0xFFFF00FF, // Not yet implemented,
            inversePrimary = 0xFFFF00FF, // Not yet implemented,
            tertiary = 0xFFFF00FF, // Not yet implemented,
            onTertiary = 0xFFFF00FF, // Not yet implemented,
            tertiaryContainer = 0xFFFF00FF, // Not yet implemented,
            onTertiaryContainer = 0xFFFF00FF, // Not yet implemented,
            surfaceTint = 0xFFFF00FF, // Not yet implemented,
            inverseSurface = 0xFFFF00FF, // Not yet implemented,
            inverseOnSurface = 0xFFFF00FF, // Not yet implemented,
            errorContainer = 0xFFFF00FF, // Not yet implemented,
            onErrorContainer = 0xFFFF00FF, // Not yet implemented,
            scrim = 0xFFFF00FF, // Not yet implemented,
            surfaceBright = 0xFFFF00FF, // Not yet implemented,
            surfaceDim = 0xFFFF00FF, // Not yet implemented,
            surfaceContainer = 0xFFFF00FF, // Not yet implemented,
            surfaceContainerHigh = 0xFFFF00FF, // Not yet implemented,
            surfaceContainerHighest = 0xFFFF00FF, // Not yet implemented,
            surfaceContainerLow = 0xFFFF00FF, // Not yet implemented,
            surfaceContainerLowest = 0xFFFF00FF, // Not yet implemented,
        )
    }
}
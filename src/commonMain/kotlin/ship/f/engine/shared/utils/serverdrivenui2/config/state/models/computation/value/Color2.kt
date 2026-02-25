package ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Color2")
sealed class Color2 : Value {
    @Serializable
    @SerialName("Primary")
    data object Primary : Color2()
    @Serializable
    @SerialName("OnPrimary")
    data object OnPrimary : Color2()
    @Serializable
    @SerialName("PrimaryContainer")
    data object PrimaryContainer : Color2()
    @Serializable
    @SerialName("OnPrimaryContainer")
    data object OnPrimaryContainer : Color2()
    @Serializable
    @SerialName("InversePrimary")
    data object InversePrimary : Color2()

    @Serializable
    @SerialName("Secondary")
    data object Secondary : Color2()
    @Serializable
    @SerialName("OnSecondary")
    data object OnSecondary : Color2()
    @Serializable
    @SerialName("SecondaryContainer")
    data object SecondaryContainer : Color2()
    @Serializable
    @SerialName("OnSecondaryContainer")
    data object OnSecondaryContainer : Color2()

    @Serializable
    @SerialName("Tertiary")
    data object Tertiary : Color2()
    @Serializable
    @SerialName("OnTertiary")
    data object OnTertiary : Color2()
    @Serializable
    @SerialName("TertiaryContainer")
    data object TertiaryContainer : Color2()
    @Serializable
    @SerialName("OnTertiaryContainer")
    data object OnTertiaryContainer : Color2()

    @Serializable
    @SerialName("Background")
    data object Background : Color2()
    @Serializable
    @SerialName("OnBackground")
    data object OnBackground : Color2()

    @Serializable
    @SerialName("Surface")
    data object Surface : Color2()
    @Serializable
    @SerialName("SurfaceVariant")
    data object SurfaceVariant : Color2()
    @Serializable
    @SerialName("OnSurface")
    data object OnSurface : Color2()
    @Serializable
    @SerialName("OnSurfaceVariant")
    data object OnSurfaceVariant : Color2()
    @Serializable
    @SerialName("SurfaceTint")
    data object SurfaceTint : Color2()
    @Serializable
    @SerialName("InverseSurface")
    data object InverseSurface : Color2()
    @Serializable
    @SerialName("InverseOnSurface")
    data object InverseOnSurface : Color2()

    @Serializable
    @SerialName("Error")
    data object Error : Color2()
    @Serializable
    @SerialName("OnError")
    data object OnError : Color2()
    @Serializable
    @SerialName("ErrorContainer")
    data object ErrorContainer : Color2()
    @Serializable
    @SerialName("OnErrorContainer")
    data object OnErrorContainer : Color2()

    @Serializable
    @SerialName("Outline")
    data object Outline : Color2()
    @Serializable
    @SerialName("OutlineVariant")
    data object OutlineVariant : Color2()

    @Serializable
    @SerialName("Scrim")
    data object Scrim : Color2()
    @Serializable
    @SerialName("SurfaceDim")
    data object SurfaceDim : Color2()
    @Serializable
    @SerialName("SurfaceBright")
    data object SurfaceBright : Color2()
    @Serializable
    @SerialName("SurfaceContainer")
    data object SurfaceContainer : Color2()
    @Serializable
    @SerialName("SurfaceContainerHigh")
    data object SurfaceContainerHigh : Color2()
    @Serializable
    @SerialName("SurfaceContainerHighest")
    data object SurfaceContainerHighest : Color2()
    @Serializable
    @SerialName("SurfaceContainerLow")
    data object SurfaceContainerLow : Color2()
    @Serializable
    @SerialName("SurfaceContainerLowest")
    data object SurfaceContainerLowest : Color2()

    @Serializable
    @SerialName("Unspecified")
    data object Unspecified : Color2()

    @Serializable
    @SerialName("Transparent")
    data object Transparent : Color2()

    @Serializable
    @SerialName("AlphaColor2")
    data class AlphaColor2(
        val color: Color2,
        val alpha: Float,
    ) : Color2()

    @Serializable
    @SerialName("CustomColor2")
    data class CustomColor2(
        val label: String,
        val color: Long,
        val alpha: Float,
    ) : Color2()



    @Serializable
    @SerialName("Gradient")
    data class Gradient(
        val direction: GradientDirection = GradientDirection.Vertical,
        val colors: List<AlphaColor2>,
    ): Color2() {

        @Serializable
        @SerialName("GradientDirection")
        sealed class GradientDirection {
            @Serializable
            @SerialName("Horizontal")
            data object Horizontal : GradientDirection()
            @Serializable
            @SerialName("Vertical")
            data object Vertical : GradientDirection()
        }
    }
}
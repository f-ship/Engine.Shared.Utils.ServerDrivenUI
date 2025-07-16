package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Align {
    @Serializable
    @SerialName("StartTop")
    data object TopStart : Align()

    @Serializable
    @SerialName("CenterTop")
    data object TopCenter : Align()

    @Serializable
    @SerialName("EndTop")
    data object TopEnd : Align()

    @Serializable
    @SerialName("StartCenter")
    data object CenterStart : Align()

    @Serializable
    @SerialName("CenterCenter")
    data object Center : Align()

    @Serializable
    @SerialName("EndCenter")
    data object CenterEnd : Align()

    @Serializable
    @SerialName("StartBottom")
    data object BottomStart : Align()

    @Serializable
    @SerialName("CenterBottom")
    data object BottomCenter : Align()

    @Serializable
    @SerialName("EndBottom")
    data object BottomEnd : Align()
}
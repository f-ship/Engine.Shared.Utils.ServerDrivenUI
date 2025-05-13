package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
sealed class Arrangement {
    @Serializable
    @SerialName("Start")
    data object Start : Arrangement()
    @Serializable
    @SerialName("End")
    data object End : Arrangement()
    @Serializable
    @SerialName("Center")
    data object Center : Arrangement()
    @Serializable
    @SerialName("Flex")
    data object Flex : Arrangement()
}
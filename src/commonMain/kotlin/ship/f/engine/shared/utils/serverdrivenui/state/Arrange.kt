package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
sealed class Arrange {
    @Serializable
    @SerialName("Start")
    data object Start : Arrange()
    @Serializable
    @SerialName("End")
    data object End : Arrange()
    @Serializable
    @SerialName("Center")
    data object Center : Arrange()
    @Serializable
    @SerialName("Flex")
    data object Flex : Arrange()
    @Serializable
    @SerialName("SpaceBetween")
    data object SpaceBetween : Arrange()
}
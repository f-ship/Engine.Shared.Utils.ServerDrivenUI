package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable

@Serializable
sealed class Size

@Serializable
data class Fixed(
    val height: Int,
    val width: Int,
) : Size()

@Serializable
data class Fill(
    val horizontalFill: Float? = null,
    val verticalFill: Float? = null,
) : Size()

@Serializable
data class Weight(
    val horizontalWeight: Float? = null,
    val verticalWeight: Float? = null,
) : Size()

@Serializable
data object Window : Size()

@Serializable
data object DefaultSize : Size()

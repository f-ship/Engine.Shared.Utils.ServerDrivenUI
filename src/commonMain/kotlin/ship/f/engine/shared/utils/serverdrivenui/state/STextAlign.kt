package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable

@Serializable
sealed class STextAlign {
    data object Start : STextAlign()
    data object End : STextAlign()
    data object Center : STextAlign()
}
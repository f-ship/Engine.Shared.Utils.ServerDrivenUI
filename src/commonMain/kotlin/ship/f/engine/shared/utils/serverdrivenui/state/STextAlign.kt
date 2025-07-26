package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable

@Serializable
sealed class STextAlign {
    @Serializable
    data object Start : STextAlign()
    @Serializable
    data object End : STextAlign()
    @Serializable
    data object Center : STextAlign()
}
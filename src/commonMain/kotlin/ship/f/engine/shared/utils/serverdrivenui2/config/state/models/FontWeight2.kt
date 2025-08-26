package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class FontWeight2 {
    @Serializable
    data object Normal2 : FontWeight2()

    @Serializable
    data object SemiBold2 : FontWeight2()

    @Serializable
    data object Bold2 : FontWeight2()

    @Serializable
    data object ExtraBold2 : FontWeight2()
}
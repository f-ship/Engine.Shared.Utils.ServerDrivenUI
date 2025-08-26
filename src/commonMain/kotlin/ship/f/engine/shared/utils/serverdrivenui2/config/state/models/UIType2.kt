package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class UIType2 {
    @Serializable
    data object Primary2 : UIType2()
    @Serializable
    data object Secondary2 : UIType2()
    @Serializable
    data object Tertiary2 : UIType2()
}

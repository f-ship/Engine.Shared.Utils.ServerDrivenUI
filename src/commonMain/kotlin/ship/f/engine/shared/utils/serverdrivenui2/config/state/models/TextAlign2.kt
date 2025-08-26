package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class TextAlign2 {
    @Serializable
    data object Left2 : TextAlign2()
    @Serializable
    data object Right2 : TextAlign2()
    @Serializable
    data object Start2 : TextAlign2()
    @Serializable
    data object End2 : TextAlign2()
    @Serializable
    data object Center2 : TextAlign2()
    @Serializable
    data object Justify2 : TextAlign2()
    @Serializable
    data object Unspecified2 : TextAlign2()
}
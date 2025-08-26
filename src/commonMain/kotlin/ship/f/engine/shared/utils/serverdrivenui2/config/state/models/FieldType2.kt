package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class FieldType2 {
    @Serializable
    data object Text : FieldType2()

    @Serializable
    data object Number : FieldType2()

    @Serializable
    data object Password : FieldType2()
}

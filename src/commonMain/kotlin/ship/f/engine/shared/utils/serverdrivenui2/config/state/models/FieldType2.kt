package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FieldType2")
sealed class FieldType2 {
    @Serializable
    @SerialName("Text")
    data object Text : FieldType2()

    @Serializable
    @SerialName("Number")
    data object Number : FieldType2()

    @Serializable
    @SerialName("Password")
    data object Password : FieldType2()
}

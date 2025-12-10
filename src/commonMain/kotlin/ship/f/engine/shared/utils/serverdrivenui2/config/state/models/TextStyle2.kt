package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TextStyle2")
sealed class TextStyle2 {
    @Serializable
    @SerialName("TitleLarge2")
    data object TitleLarge2 : TextStyle2()

    @Serializable
    @SerialName("TitleMedium2")
    data object TitleMedium2 : TextStyle2()

    @Serializable
    @SerialName("TitleSmall2")
    data object TitleSmall2 : TextStyle2()

    @Serializable
    @SerialName("HeadlineLarge2")
    data object HeadlineLarge2 : TextStyle2()

    @Serializable
    @SerialName("HeadlineMedium2")
    data object HeadlineMedium2 : TextStyle2()

    @Serializable
    @SerialName("HeadlineSmall2")
    data object HeadlineSmall2 : TextStyle2()

    @Serializable
    @SerialName("DisplayLarge2")
    data object DisplayLarge2 : TextStyle2()

    @Serializable
    @SerialName("DisplayMedium2")
    data object DisplayMedium2 : TextStyle2()

    @Serializable
    @SerialName("DisplaySmall2")
    data object DisplaySmall2 : TextStyle2()

    @Serializable
    @SerialName("BodyLarge2")
    data object BodyLarge2 : TextStyle2()

    @Serializable
    @SerialName("BodyMedium2")
    data object BodyMedium2 : TextStyle2()

    @Serializable
    @SerialName("BodySmall2")
    data object BodySmall2 : TextStyle2()

    @Serializable
    @SerialName("LabelLarge2")
    data object LabelLarge2 : TextStyle2()

    @Serializable
    @SerialName("LabelMedium2")
    data object LabelMedium2 : TextStyle2()

    @Serializable
    @SerialName("LabelSmall2")
    data object LabelSmall2 : TextStyle2()

    @Serializable
    @SerialName("Custom2")
    data class Custom2(
        val size: Int,
        val lineHeight: Int,
        val weight: FontWeight2,
    ) : TextStyle2()
}
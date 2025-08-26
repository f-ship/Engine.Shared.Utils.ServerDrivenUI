package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class TextStyle2 {
    @Serializable
    data object TitleLarge2 : TextStyle2()

    @Serializable
    data object TitleMedium2 : TextStyle2()

    @Serializable
    data object TitleSmall2 : TextStyle2()

    @Serializable
    data object HeadlineLarge2 : TextStyle2()

    @Serializable
    data object HeadlineMedium2 : TextStyle2()

    @Serializable
    data object HeadlineSmall2 : TextStyle2()

    @Serializable
    data object DisplayLarge2 : TextStyle2()

    @Serializable
    data object DisplayMedium2 : TextStyle2()

    @Serializable
    data object DisplaySmall2 : TextStyle2()

    @Serializable
    data object BodyLarge2 : TextStyle2()

    @Serializable
    data object BodyMedium2 : TextStyle2()

    @Serializable
    data object BodySmall2 : TextStyle2()

    @Serializable
    data object LabelLarge2 : TextStyle2()

    @Serializable
    data object LabelMedium2 : TextStyle2()

    @Serializable
    data object LabelSmall2 : TextStyle2()
}
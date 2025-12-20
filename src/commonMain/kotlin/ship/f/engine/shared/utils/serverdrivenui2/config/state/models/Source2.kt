package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3

@Serializable
@SerialName("Source2")
sealed class Source2 {
    abstract val location: String
    @Serializable
    @SerialName("Url2")
    data class Url2(
        override val location: String,
    ) : Source2()

    @Serializable
    @SerialName("Local2")
    data class Local2(
        override val location: String,
    ) : Source2()

    @Serializable
    @SerialName("Material2")
    data class Material2(
        override val location: String,
    ) : Source2()

    @Serializable
    @SerialName("Resource2")
    data class Resource2(
        override val location: String,
    ) : Source2()

    @Serializable
    data class LiveUrl2(
        val liveValue: LiveValue3,
    ) : Source2() {
        override val location: String
            get() = error("Do Not Use")
    }
}
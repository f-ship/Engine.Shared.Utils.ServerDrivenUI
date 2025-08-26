package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class Source2 {
    abstract val location: String
    @Serializable
    data class Url2(
        override val location: String,
    ) : Source2()

    @Serializable
    data class Local2(
        override val location: String,
    ) : Source2()

    @Serializable
    data class Resource2(
        override val location: String,
    ) : Source2()
}
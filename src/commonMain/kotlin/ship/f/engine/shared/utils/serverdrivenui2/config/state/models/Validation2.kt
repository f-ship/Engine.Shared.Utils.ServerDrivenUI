package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
data class Validation2(
    val regex: String? = null,
    val error: String? = null,
    val isRequired: Boolean = false
)
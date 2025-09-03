package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2

@Serializable
@SerialName("UrlMeta2")
data class UrlMeta2(
    override val metaId: Id2.MetaId2,
    val url: String,
    val urlIntercept: String?,
    val params: Map<String, String>?,
) : Meta2()

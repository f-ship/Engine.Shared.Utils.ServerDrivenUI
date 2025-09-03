package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2

@Serializable
@SerialName("JsonMeta2")
data class JsonMeta2(
    override val metaId: Id2.MetaId2,
    val json: JsonElement,
) : Meta2()
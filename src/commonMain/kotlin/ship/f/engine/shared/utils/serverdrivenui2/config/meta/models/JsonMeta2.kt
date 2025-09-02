package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2

@Serializable
data class JsonMeta2(
    override val id: Id2.MetaId2,
    val json: JsonElement,
) : Meta2()
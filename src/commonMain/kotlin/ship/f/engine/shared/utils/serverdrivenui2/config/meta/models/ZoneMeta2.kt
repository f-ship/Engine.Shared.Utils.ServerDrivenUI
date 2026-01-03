package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.StringValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Value

@Serializable
@SerialName("ZoneViewModel2")
data class ZoneViewModel2(
    override val metaId: Id2.MetaId2 = autoMetaId2(),
    val map: MutableMap<String, Property> = mutableMapOf()
) : Meta2() {
    @Serializable
    @SerialName("Property")
    sealed class Property {
        @Serializable
        @SerialName("StringProperty")
        data class StringProperty(val value: String) : Property()

        @Serializable
        @SerialName("BooleanProperty")
        data class BooleanProperty(val value: Boolean) : Property()

        @Serializable
        @SerialName("IntProperty")
        data class IntProperty(val value: Int) : Property()

        @Serializable
        @SerialName("MultiProperty")
        data class MultiProperty(val value: List<Property> = listOf()) : Property()
    }
}

@Serializable
@SerialName("ZoneViewModel3")
data class ZoneViewModel3(
    override val metaId: Id2.MetaId2 = autoMetaId2(),
    val map: MutableMap<String, Value> = mutableMapOf()
) : Meta2() {
    companion object {
        fun MutableMap<String, Value>.getString(property: String) = (get(property) as StringValue).value
    }
}

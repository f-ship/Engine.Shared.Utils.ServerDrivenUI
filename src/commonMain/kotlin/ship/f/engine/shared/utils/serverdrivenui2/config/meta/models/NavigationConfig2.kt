package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2

@Serializable
@SerialName("NavigationConfig2")
data class NavigationConfig2(
    override val metaId: MetaId2 = autoMetaId2(),
    val operation: StateOperation2,
) : Meta2() {

    @Serializable
    @SerialName("StateOperation2")
    sealed class StateOperation2 {
        abstract val stateId: StateId2

        @Serializable
        @SerialName("InsertionOperation2")
        sealed class InsertionOperation2 : StateOperation2() {

            abstract val inside: StateId2

            @Serializable
            @SerialName("Start2")
            data class Start2(
                override val inside: StateId2,
                override val stateId: StateId2,
            ) : InsertionOperation2()

            @Serializable
            @SerialName("End2")
            data class End2(
                override val inside: StateId2,
                override val stateId: StateId2,
            ) : InsertionOperation2()

            @Serializable
            @SerialName("Before2")
            data class Before2(
                override val inside: StateId2,
                val before: StateId2,
                override val stateId: StateId2,
            ) : InsertionOperation2() {
                val offset = 0
            }

            @Serializable
            @SerialName("After2")
            data class After2(
                override val inside: StateId2,
                val after: StateId2,
                override val stateId: StateId2,
            ) : InsertionOperation2() {
                val offset = 1
            }
        }

        @Serializable
        @SerialName("Push2")
        data class Push2(
            override val stateId: StateId2,
        ) : StateOperation2()

        @Serializable
        @SerialName("Replace2")
        data class Replace2(
            val replace: StateId2,
            override val stateId: StateId2,
        ) : StateOperation2()
    }
}


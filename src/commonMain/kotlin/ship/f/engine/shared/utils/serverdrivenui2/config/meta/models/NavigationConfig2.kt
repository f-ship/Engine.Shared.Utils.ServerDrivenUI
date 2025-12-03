package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Navigate2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.RemoteAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

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

            @Serializable
            @SerialName("Remove2")
            data class Remove2(
                override val inside: StateId2,
                override val stateId: StateId2,
            ) : InsertionOperation2()
        }

        @Serializable
        @SerialName("Push2")
        data class Push2(
            override val stateId: StateId2,
        ) : StateOperation2()

        @Serializable
        @SerialName("Flow2")
        data class Flow2(
            override val stateId: StateId2 = StateId2(),
            val flow: List<StateId2>,
            val push: Boolean = true,
        ) : StateOperation2()

        @Serializable
        @SerialName("Next2")
        data class Next2(
            override val stateId: StateId2 = StateId2(),
            val idempotentKey: String? = null,
        ) : StateOperation2()

        @Serializable
        @SerialName("PushDialog2")
        data class PushDialog2(
            override val stateId: StateId2,
            val targetId2: StateId2,
        ) : StateOperation2()

        @Serializable
        @SerialName("Replace2")
        data class Replace2(
            val replace: StateId2,
            override val stateId: StateId2,
        ) : StateOperation2()


        @Serializable
        @SerialName("ReplaceChild2")
        data class ReplaceChild2(
            val container: StateId2,
            override val stateId: StateId2,
            val addToBackStack: Boolean = false,
        ) : StateOperation2()

        @Serializable
        @SerialName("Swap2")
        data class Swap2(
            val swap: State2,
            override val stateId: StateId2,
        ) : StateOperation2() {
            companion object {
                fun State2.swap2() = RemoteAction2(
                    targetStateId = id,
                    action = Navigate2(
                        config = NavigationConfig2(
                            operation = Swap2(
                                swap = this,
                                stateId = id
                            )
                        )
                    )
                )
            }
        }
    }
}


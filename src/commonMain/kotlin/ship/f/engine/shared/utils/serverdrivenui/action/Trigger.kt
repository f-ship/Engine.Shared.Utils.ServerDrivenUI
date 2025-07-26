package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.ID
import ship.f.engine.shared.utils.serverdrivenui.action.Meta.Companion.none
import ship.f.engine.shared.utils.serverdrivenui.ext.id

@Serializable
@SerialName("TriggerAction")
sealed class Trigger {
    abstract val metaID: ID
    abstract val action: Action

    @Serializable
    @SerialName("OnClickTrigger")
    data class OnClickTrigger(
        override val metaID: ID = id("none"),
        override val action: Action,
    ) : Trigger()

    @Serializable
    @SerialName("OnHoldTrigger")
    data class OnHoldTrigger(
        override val metaID: ID = none,
        override val action: Action,
    ) : Trigger()

    @Serializable
    @SerialName("OnPressTrigger") // TODO what is the difference between this and OnClickTrigger
    data class OnPressTrigger(
        override val metaID: ID = none,
        override val action: Action,
    ) : Trigger()

    @Serializable
    @SerialName("OnFieldUpdate")
    data class OnFieldUpdateTrigger(
        override val metaID: ID = none,
        override val action: Action,
    ) : Trigger()

    @Serializable
    @SerialName("OnToggleUpdateTrigger")
    data class OnToggleUpdateTrigger(
        override val metaID: ID = none,
        override val action: Action,
    ) : Trigger()

    @Serializable
    @SerialName("OnTickUpdateTrigger")
    data class OnCheckUpdateTrigger(
        override val metaID: ID = none,
        override val action: Action,
    ) : Trigger()

    @Serializable
    @SerialName("OnStateUpdate")
    data class OnStateUpdateTrigger(
        override val metaID: ID = none,
        override val action: Action,
    ) : Trigger()

    @Serializable
    @SerialName("OnMetaUpdateTrigger")
    data class OnMetaUpdateTrigger(
        override val metaID: ID,
        override val action: Action
    ) : Trigger()

    @Serializable
    @SerialName("OnInitialRenderTrigger")
    data class OnInitialRenderTrigger(
        override val metaID: ID = none,
        override val action: Action,
    ) : Trigger()

    @Serializable
    @SerialName("DeferredTrigger")
    data class DeferredTrigger(
        override val metaID: ID,
        override val action: Action,
        val group: ID = none,
    ) : Trigger()
}
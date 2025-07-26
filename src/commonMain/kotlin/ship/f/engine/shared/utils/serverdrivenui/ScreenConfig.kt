package ship.f.engine.shared.utils.serverdrivenui

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.action.DeferredAction
import ship.f.engine.shared.utils.serverdrivenui.action.Meta
import ship.f.engine.shared.utils.serverdrivenui.action.RemoteAction
import ship.f.engine.shared.utils.serverdrivenui.action.Trigger
import ship.f.engine.shared.utils.serverdrivenui.action.Trigger.DeferredTrigger
import ship.f.engine.shared.utils.serverdrivenui.client.ClientHolder.getClient
import ship.f.engine.shared.utils.serverdrivenui.ext.auto
import ship.f.engine.shared.utils.serverdrivenui.state.ColorSchemeState
import ship.f.engine.shared.utils.serverdrivenui.state.ComponentState
import ship.f.engine.shared.utils.serverdrivenui.state.State
import ship.f.engine.shared.utils.serverdrivenui.state.WidgetState

/**
 * Main configuration for the screen
 */
@Serializable()
@SerialName("ScreenConfig")
data class ScreenConfig(
    val id: ID = auto(),
    val lightColorScheme: ColorSchemeState? = null,
    val darkColorScheme: ColorSchemeState? = null,
    val children: List<Element<out State>> = emptyList(),
) {
    @Serializable
    @SerialName("ID")
    data class ID(val id: String, val scope: String)

    @Serializable()
    @SerialName("Element")
    sealed class Element<S : State> {
        abstract val id: ID
        abstract val state: S
        abstract val fallback: Fallback

        abstract val triggers: List<Trigger>
        abstract val listeners: List<RemoteAction>

        abstract val metas: Map<ID, Meta>

        fun updateElement(
            state: State = this.state,
            listeners: List<RemoteAction> = this.listeners,
        ) = when (this) {
            is Component<*> -> update(
                state = state,
                listeners = listeners,
            )
            is Widget<*> -> update(
                state = state,
                listeners = listeners,
            )
        }
        inline fun <reified T : Trigger> trigger() {
            val client = getClient()
            triggers.filterIsInstance<T>().forEach { triggerAction ->
                when(triggerAction){
                    is DeferredTrigger -> Unit
                    else -> {
                        triggerAction.action.execute(
                            element = this,
                            client = client,
                            meta = client.metaMap[triggerAction.metaID] ?: Meta.None(),
                        )
                    }
                }
            }
        }

        fun deferredTrigger(restoredElement: Element<out State>? = null) {
            val client = getClient()
            triggers.filterIsInstance<DeferredTrigger>().forEach { triggerAction ->
                if (client.deferredActions[triggerAction.group] == null) {
                    client.deferredActions[triggerAction.group] = mutableListOf()
                }
                client.deferredActions[triggerAction.group]!!.add(
                    DeferredAction(
                        action = triggerAction.action,
                        id = id,
                        metaID = triggerAction.metaID,
                        restoredElement = restoredElement,
                    )
                )
            }
        }
    }

    @Serializable
    @SerialName("Widget")
    data class Widget<S : WidgetState>(
        override val id: ID = auto(),
        override val state: S,
        override val fallback: Fallback = Fallback.Hide,
        override val triggers: List<Trigger> = emptyList(),
        override val listeners: List<RemoteAction> = emptyList(),
        override val metas: Map<ID, Meta> = mapOf(),
    ) : Element<WidgetState>() {
        fun update(
            state: State = this.state,
            listeners: List<RemoteAction> = this.listeners,
        ) = copy(
            state = state as S,
            listeners = listeners,
        )
        fun update(block: S.() -> S): Widget<S> {
            return copy(state = block(this.state))
        }
    }

    @Serializable
    @SerialName("Component")
    data class Component<S : ComponentState>(
        override val id: ID = auto(),
        override val state: S,
        override val fallback: Fallback = Fallback.Hide,
        override val triggers: List<Trigger> = emptyList(),
        override val listeners: List<RemoteAction> = emptyList(),
        override val metas: Map<ID, Meta> = mapOf(),
    ) : Element<ComponentState>() {
        fun update(
            state: State = this.state,
            listeners: List<RemoteAction> = this.listeners,
        ) = copy(
            state = state as S,
            listeners = listeners,
        )
        fun update(block: S.() -> S): Component<S> {
            return copy(state = block(this.state))
        }
    }

    @Serializable
    @SerialName("Fallback")
    sealed class Fallback {

        @Serializable
        @SerialName("UI")
        data class UI(
            val id: ID,
            val state: State,
        ) : Fallback()

        @Serializable
        @SerialName("Hide")
        data object Hide : Fallback()

        @Serializable
        @SerialName("OptionalUpdate")
        data object OptionalUpdate : Fallback()

        @Serializable
        @SerialName("RequireUpdate")
        data object RequireUpdate : Fallback()
    }

    companion object {
        val empty = ScreenConfig()
    }
}

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
import ship.f.engine.shared.utils.serverdrivenui.ext.autoScreenId
import ship.f.engine.shared.utils.serverdrivenui.ext.id
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
    val id: ScreenId = autoScreenId(),
    val lightColorScheme: ColorSchemeState? = null,
    val darkColorScheme: ColorSchemeState? = null,
    val children: List<Element<out State>> = emptyList(),
) {
    @Serializable
    sealed class ID {
        abstract val id: String
        abstract val scope: String
    }

    @Serializable
    @SerialName("ElementId")
    data class ElementId(
        override val id: String,
        override val scope: String,
    ) : ID()

    @Serializable
    @SerialName("MetaId")
    data class MetaId(
        override val id: String,
        override val scope: String,
    ) : ID()

    @Serializable
    @SerialName("ScreenConfigId")
    data class ScreenId(
        override val id: String,
        override val scope: String,
    ) : ID()

    @Serializable
    @SerialName("Element")
    sealed class Element<S : State> {
        abstract val id: ElementId
        abstract val activeScope: String
        abstract val state: S
        abstract val fallback: Fallback

        abstract val triggers: List<Trigger>
        abstract val listeners: List<RemoteAction>

        abstract val metas: Map<MetaId, Meta>

        fun updateElement(
            state: State = this.state,
            listeners: List<RemoteAction> = this.listeners,
            activeScope: String = this.activeScope,
        ) = when (this) {
            is Component<*> -> update(
                state = state,
                listeners = listeners,
                activeScope = activeScope,
            )

            is Widget<*> -> update(
                state = state,
                listeners = listeners,
                activeScope = activeScope,
            )
        }

        inline fun <reified T : Trigger> trigger() {
            val client = getClient()
            triggers.filterIsInstance<T>().forEach { triggerAction ->
                when (triggerAction) {
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

        companion object {
            val none = id("None")
            const val DEFAULT_ELEMENT_SCOPE = "DefaultElementScope"
        }
    }

    @Serializable
    @SerialName("Widget")
    @Suppress("UNCHECKED_CAST")
    data class Widget<S : WidgetState>(
        override val id: ElementId = auto(),
        override val activeScope: String = id.scope, // TODO this is probably useless to be honest
        override val state: S,
        override val fallback: Fallback = Fallback.Hide,
        override val triggers: List<Trigger> = emptyList(),
        override val listeners: List<RemoteAction> = emptyList(),
        override val metas: Map<MetaId, Meta> = mapOf(),
    ) : Element<WidgetState>() {
        fun update(
            state: State = this.state,
            listeners: List<RemoteAction> = this.listeners,
            activeScope: String = this.activeScope,
        ) = copy(
            state = state as S,
            listeners = listeners,
            activeScope = activeScope,
        )

        fun update(block: S.() -> S): Widget<S> {
            return copy(state = block(this.state))
        }
    }

    @Serializable
    @SerialName("Component")
    @Suppress("UNCHECKED_CAST")
    data class Component<S : ComponentState>(
        override val id: ElementId = auto(),
        override val activeScope: String = id.scope,
        override val state: S,
        override val fallback: Fallback = Fallback.Hide,
        override val triggers: List<Trigger> = emptyList(),
        override val listeners: List<RemoteAction> = emptyList(),
        override val metas: Map<MetaId, Meta> = mapOf(),
    ) : Element<ComponentState>() {
        fun update(
            state: State = this.state,
            listeners: List<RemoteAction> = this.listeners,
            activeScope: String = this.activeScope,
        ) = copy(
            state = state as S,
            listeners = listeners,
            activeScope = activeScope,
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
            val id: ElementId,
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
        const val DEFAULT_SCREEN_SCOPE = "DefaultScreenScope"
        fun screenId(value: String, scope: String = DEFAULT_SCREEN_SCOPE) = ScreenId(value, scope)
    }
}

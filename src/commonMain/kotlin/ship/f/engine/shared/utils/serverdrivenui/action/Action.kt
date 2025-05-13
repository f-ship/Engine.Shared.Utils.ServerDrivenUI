package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Meta.None
import ship.f.engine.shared.utils.serverdrivenui.measureInMillis
import ship.f.engine.shared.utils.serverdrivenui.state.ComponentState
import ship.f.engine.shared.utils.serverdrivenui.state.State
import ship.f.engine.shared.utils.serverdrivenui.state.Value
import ship.f.engine.shared.utils.serverdrivenui.state.WidgetState

fun <K, V> Map<K,V>.fGet(key: K): V = get(key) ?: error("Key $key not found in $this")

interface Client {
    val stateMap: MutableMap<ID, StateHolder<State>>
    val elementMap: MutableMap<ID, Element> // TODO May not need this

    @Serializable
    data class StateHolder<T : State>(
        val state: T,
        val listeners: List<RemoteAction> = listOf(),
    ) {
        @Serializable
        data class RemoteAction(
            val action: Action,
            val id: ID = ID(id = "", scope = "") // Implemented at runtime
        )
    }

    // Need some way to properly register updates for derived states
    fun updateState(id: ID, state: State) = measureInMillis("updateState: $id") {
        val stateHolder = stateMap.fGet(id).copy(state = state)
        stateMap[id] = stateHolder
        postUpdateHook(id = id, stateHolder = stateHolder)
        stateMap.fGet(id).listeners.forEach { listener ->
            val s = stateMap.fGet(listener.id).state
            val scope = when(s){
                is ComponentState -> Subject.Component(s, listener.id)
                is WidgetState -> Subject.Widget(s, listener.id)
            }
            listener.action.execute(scope, this)
            postUpdateHook(id = listener.id, stateHolder = stateMap.fGet(listener.id))
        }
    }

    fun postUpdateHook(id: ID, stateHolder: StateHolder<State>)
}
sealed class Subject {
    data class Component(val component: ComponentState, val id: ID) : Subject()
    data class Widget(val widget: WidgetState, val id: ID) : Subject()
    data class Screen(val screen: List<Widget>, val id: ID) : Subject()
    data class All(val targets: List<Subject>) : Subject()
}

@Serializable
data class Target2(
    val id: ID,
    val descendants: Descendants = Descendants.None,
) {
    @Serializable
    sealed class Descendants {
        @Serializable
        @SerialName("None")
        object None : Descendants()
        @Serializable
        @SerialName("Immediate")
        object Immediate : Descendants()
        @Serializable
        @SerialName("All")
        object All : Descendants()
    }
}

/**
 * There are two types of actions
 * For each action a user can trigger, we must also be able to synthetically trigger the same action
 * This means we'll need to wrap each action (Seems weired similar to the nonsense theo talked about at twitch)
 * Generic actions, i.e.
 *     - copy value from a field to the clipboard
 *     - update a text field value
 *     - send a request to the server with states
 */
@Serializable
sealed class Action {
    abstract val targetIds: List<Target2>

    abstract fun execute(
        subject: Subject,
        client: Client,
        meta: Meta = None,
    )

    @Serializable
    @SerialName("Navigate")
    data class Navigate(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }

    }

    @Serializable
    @SerialName("Refresh")
    data class Refresh(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }

    }

    @Serializable
    @SerialName("Copy")
    data class Copy(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }

    }

    @Serializable
    @SerialName("UpdateFieldState")
    data class UpdateFieldState(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            when (subject) {
                is Subject.All -> subject.targets.forEach {
                    execute(it, client, meta)
                }
                is Subject.Component -> {
                    client.updateState( subject.id, subject.component)
                }
                is Subject.Screen -> {
                    subject.screen.forEach {
                        client.updateState( it.id, it.widget)
                    }
                }
                is Subject.Widget -> {
                    client.updateState( subject.id, subject.widget)
                }
            }
        }
    }

    // If searches the tree to ensure other visibilities of the same group match the one of the event Visibility(val group: String, val value: String)
    @Serializable
    @SerialName("UpdateVisibility")
    data class UpdateVisibility(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("UpdateEnabled")
    data class UpdateEnabled(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("UpdateToggled")
    data class UpdateToggled(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            when (subject) {
                is Subject.All -> subject.targets.forEach {
                    execute(it, client, meta)
                }
                is Subject.Component -> {
                    client.updateState( subject.id, subject.component)
                }
                is Subject.Screen -> {
                    subject.screen.forEach {
                        client.updateState( it.id, it.widget)
                    }
                }
                is Subject.Widget -> {
                    client.updateState( subject.id, subject.widget)
                }
            }
        }
    }

    @Serializable
    @SerialName("UpdateState")
    data class UpdateState(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            when (subject) {
                is Subject.All -> subject.targets.forEach {
                    execute(it, client, meta)
                }
                is Subject.Component -> {
                    client.updateState( subject.id, subject.component)
                }
                is Subject.Screen -> {
                    subject.screen.forEach {
                        client.updateState( it.id, it.widget)
                    }
                }
                is Subject.Widget -> {
                    client.updateState( subject.id, subject.widget)
                }
            }
        }
    }

    @Serializable
    @SerialName("SendState")
    data class SendState(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            subject: Subject,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("UpdateValue")
    data class UpdateValue(
        override val targetIds: List<Target2> = listOf(),
    ) : Action() {
        override fun execute(
            scope: Subject, // TODO depreciate this, this was an error as it can only be hardcoded, use targetIds instead, maybe it's still useful actually
            client: Client,
            meta: Meta,
        ) {
            when(scope) {
                is Subject.All -> TODO()
                is Subject.Component -> {
                    val toUpdate = client.stateMap.fGet(scope.id).state as? Value<*>
                    val update = client.stateMap.fGet(targetIds.first().id).state as? Value<*>
                    if (toUpdate != null && update != null){
                        val updated = toUpdate.copyValue(v = update.value)
                        client.updateState(scope.id, updated)
                    }
                }
                is Subject.Screen -> TODO()
                is Subject.Widget -> TODO()
            }
        }
    }
}

//Config Time
// Presence of OnStateUpdate adds it's action as a listener to the state holder of each of it's targets

//Run Time
// When that state updates via the client.updateState mechanism it should apply listeners
// The target should be the state that needs the action run on it
// The action can be run directly
// The action should use the targetIds value to update it's own value

//Hmm I need a cleaner way to do this
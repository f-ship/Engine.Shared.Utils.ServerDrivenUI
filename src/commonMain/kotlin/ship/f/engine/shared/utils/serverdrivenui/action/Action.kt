package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Meta.None
import ship.f.engine.shared.utils.serverdrivenui.measureInMillis
import ship.f.engine.shared.utils.serverdrivenui.state.State
import ship.f.engine.shared.utils.serverdrivenui.state.Valid
import ship.f.engine.shared.utils.serverdrivenui.state.Value

fun <K, V> Map<K,V>.fGet(key: K): V = get(key) ?: error("Key $key not found in $this")

interface Client {
    val stateMap: MutableMap<ID, StateHolder<out State>>
    val elementMap: MutableMap<ID, Element> // TODO May not need this

    var banner: Fallback?

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

    /**
     * Currently need to messily have two implementations here for the same thing as we are updating the state too many times otherwise.
     * Need to create a more intelligent way to update the state and another process to propagate state to listeners.
     * Even though the element shouldn't change outside the state, maybe I should do the state changes on that level
     */
    fun updateState(id: ID, state: State) = measureInMillis("updateState: $id") {
        stateMap.fGet(id).listeners.forEach { listener ->
            val e = elementMap.fGet(listener.id)
            listener.action.execute(e, this)
            postUpdateHook(id = listener.id, stateHolder = stateMap.fGet(listener.id))
        }
    }

    fun updateState2(id: ID, state: State) = measureInMillis("updateState: $id") {
        val oldStateHolder = stateMap.fGet(id)
        val stateHolder = StateHolder(state = state, listeners = oldStateHolder.listeners)
        stateMap[id] = stateHolder
        postUpdateHook(id = id, stateHolder = stateHolder)
        stateMap.fGet(id).listeners.forEach { listener ->
            val e = elementMap.fGet(listener.id)
            listener.action.execute(e, this)
            postUpdateHook(id = listener.id, stateHolder = stateMap.fGet(listener.id))
        }
    }

    fun postUpdateHook(id: ID, stateHolder: StateHolder<out State>)
}

@Serializable
data class Target(
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
    abstract val targetIds: List<Target>

    abstract fun execute(
        element: Element,
        client: Client,
        meta: Meta = None,
    )

    @Serializable
    @SerialName("Navigate")
    data class Navigate(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {

        }

    }

    @Serializable
    @SerialName("Refresh")
    data class Refresh(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }

    }

    @Serializable
    @SerialName("Copy")
    data class Copy(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }

    }

    sealed class Match : Action()

    // If searches the tree to ensure other visibilities of the same group match the one of the event Visibility(val group: String, val value: String)
    @Serializable
    @SerialName("UpdateVisibility")
    data class MatchVisibility(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("UpdateEnabled")
    data class MatchEnabled(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("MatchToggled")
    data class MatchToggled(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("MatchLoading")
    data class MatchLoading(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("MatchVariant")
    data class MatchVariant(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("MatchValid")
    data class MatchValid(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            val valid = targetIds.mapNotNull { client.stateMap.fGet(it.id).state as? Valid<*> }.all { it.valid == true }
            (element.state as? Valid<*>)?.copyValid(valid)?.let { state ->
                client.updateState2( element.id, state)
            }
        }
    }

    @Serializable
    @SerialName("UpdateState")
    data class UpdateState(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            client.updateState( element.id, element.state)
        }
    }

    @Serializable
    @SerialName("SendState")
    data class SendState(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("UpdateValue")
    data class UpdateValue(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element,
            client: Client,
            meta: Meta,
        ) {
            val toUpdate = client.stateMap.fGet(element.id).state as? Value<*>
            val update = client.stateMap.fGet(targetIds.first().id).state as? Value<*>
            if (toUpdate != null && update != null){
                val updated = toUpdate.copyValue(v = update.value)
                client.updateState(element.id, updated)
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
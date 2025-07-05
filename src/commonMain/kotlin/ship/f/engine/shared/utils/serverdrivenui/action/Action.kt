package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Element
import ship.f.engine.shared.utils.serverdrivenui.action.Meta.None
import ship.f.engine.shared.utils.serverdrivenui.client.Client
import ship.f.engine.shared.utils.serverdrivenui.ext.fGet
import ship.f.engine.shared.utils.serverdrivenui.state.State
import ship.f.engine.shared.utils.serverdrivenui.state.Valid
import ship.f.engine.shared.utils.serverdrivenui.state.Value

/**
 * An action is simply a reusable block of code that can be executed on a subject element.
 * Actions can be more sophisticated to handle interactions between a single subject element and multiple target elements.
 * Most basic UI operations should be able to be handled by a well-maintained library of actions.
 */
@Serializable
sealed class Action {
    abstract val targetIds: List<Target>

    abstract fun execute(
        element: Element<out State>,
        client: Client,
        meta: Meta = None,
    )

    /**
     * Currently not in use because navigation still needs to be implemented properly with a basic integration with an engine.
     * Navigation will have two types of actions which may require separate actions.
     * The first form of navigation will be an action that navigates to a new screen config
     * The second form of navigation will be able to update a portion of the current screen config.
     * This can either be done by replacing an existing element or by appending an element after another one.
     * To be able to add a component after another nested component, the parent must also be referenced.
     */
    @Serializable
    @SerialName("Navigate")
    data class Navigate(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {

        }

    }

    /**
     * It's not clear how refresh will be used yet
     * Potentially when it's triggered, the id of the screen is sent in some sort of event outside the SDUI system with the expectation of an updated screen config.
     */
    @Serializable
    @SerialName("Refresh")
    data class Refresh(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }

    }

    /**
     * The idea behind this one is that the client is used to copy the value of the element to the clipboard.
     */
    @Serializable
    @SerialName("Copy")
    data class Copy(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }

    }

    /**
     * The following set of actions are used by an element to match the state of a list of elements if they are all the same.
     */
    sealed class Match : Action()

    /**
     * If all target elements are visible, then the subject element's visibility should also be visible.
     */
    @Serializable
    @SerialName("UpdateVisibility")
    data class MatchVisibility(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    /**
     * If all target elements are enabled, then the subject element's enabled should also be enabled.
     */
    @Serializable
    @SerialName("UpdateEnabled")
    data class MatchEnabled(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    /**
     * If all target elements are toggled, then the subject element's toggled should also be toggled.
     */
    @Serializable
    @SerialName("MatchToggled")
    data class MatchToggled(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    /**
     * If all target elements are loading, then the subject element's loading should also be loading.
     */
    @Serializable
    @SerialName("MatchLoading")
    data class MatchLoading(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    /**
     * If all target elements are of a variant, then the subject element's variant should also match.
     */
    @Serializable
    @SerialName("MatchVariant")
    data class MatchVariant(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    /**
     * If all target elements are valid, then the subject element's valid should also match.
     * The subject element must also be a valid element.
     */
    @Serializable
    @SerialName("MatchValid")
    data class MatchValid(
        override val targetIds: List<Target> = listOf(),
    ) : Match() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {

            val valid = targetIds.mapNotNull { client.elementMap.fGet(it.id).state as? Valid<*> }.all { it.valid == true }
            (element.state as? Valid<*>)?.copyValid(valid)?.let { state ->
                client.updateState(element.updateState(state))
            }
        }
    }

    /**
     * UpdateState is used to update the state of a single element.
     * Even if an element updates its state locally, it should still call this whenever its state updates.
     * The reason for this is that it enables other elements to listen to the state change of the element in the propagate state method.
     */
    @Serializable
    @SerialName("UpdateState")
    data class UpdateState(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            client.updateState(element)
        }
    }

    /**
     * This will be a powerhouse method that is primarily responsible for having SDUI interface with the rest of the app/server.
     * In an SDUI landscape normal requests to a server are rarely made, instead if the client needs to interact with the server,
     * it sends back a portion of its updated UI in a flautist of elements + contextual meta-data which gets built up during a SDUI session.
     * The action itself should include the IDs of the elements and the meta-information it wants in a flatlist for the request.
     * The framework will automatically handle compiling the request and sending it off.
     */
    @Serializable
    @SerialName("SendState")
    data class SendState(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    /**
     * This action is used to update the value of a single element based on a single target element.
     */
    @Serializable
    @SerialName("UpdateValue")
    data class UpdateValue(
        override val targetIds: List<Target> = listOf(),
    ) : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            val targetElement = client.elementMap.fGet(targetIds.first().id).state as? Value<*> ?: error("Target element is not a value")
            val updatedElement = (element as Element<Value<*>>).state.copyValue(targetElement.value)
            client.updateState(element.updateState(updatedElement))
        }
    }
}
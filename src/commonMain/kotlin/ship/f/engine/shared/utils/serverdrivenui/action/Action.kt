package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*
import ship.f.engine.shared.utils.serverdrivenui.action.Meta.*
import ship.f.engine.shared.utils.serverdrivenui.client.Client
import ship.f.engine.shared.utils.serverdrivenui.ext.runIf
import ship.f.engine.shared.utils.serverdrivenui.state.State
import ship.f.engine.shared.utils.serverdrivenui.state.Valid
import ship.f.engine.shared.utils.serverdrivenui.state.Value
import ship.f.engine.shared.utils.serverdrivenui.state.Visibility

interface ElementTargetAction {
    val targetId: ElementId
}

interface ElementPublisherAction {
    val publisherId: ElementId
}

interface MultiElementTargetAction {
    val targetIds: List<ElementId>
}

interface MultiElementPublisherAction {
    val publisherIds: List<ElementId>
}

interface MetaPublisherAction {
    val publisherId: MetaId
}

interface MultiMetaPublisherAction {
    val publisherIds: List<MetaId>
}

/**
 * An action is simply a reusable block of code that can be executed on a subject element.
 * Actions can be more sophisticated to handle interactions between a single subject element and multiple target elements.
 * Most basic UI operations should be able to be handled by a well-maintained library of actions.
 */
@Serializable
sealed class Action {

    abstract fun execute(
        element: Element<out State>,
        client: Client,
        meta: Meta = None(),
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
    data object Navigate : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            //TODO Make these Navigation metas sealed class to stop this expanding
            when (meta) {
                is ScreenConfigMeta -> client.navigate(meta.screenConfig)
                is ElementConfigMeta -> client.navigate(meta.elementConfig)
                else -> Unit
            }
        }
    }

    /**
     * It's not clear how refresh will be used yet
     * Potentially when it's triggered, the id of the screen is sent in some sort of event outside the SDUI system with the expectation of an updated screen config.
     */
    @Serializable
    @SerialName("Refresh")
    data object Refresh : Action() {
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
    data object Copy : Action() {
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

    @Serializable
    @SerialName("ToggleMeta")
    data object ToggleMeta : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    @SerialName("MatchMetaVisibility")
    data object MatchMetaVisibility : Match() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            meta.runIf<FilterMeta> {
                client.metaMap[targetGroup].let { group ->
                    group?.runIf<FilterMetaStore> {
                        val isVisible = (metas.isEmpty() || metas.contains(meta.id))

                        // TODO need to actually make sure to implement visibility on components
                        (element.state as? Visibility<out State>)?.copyVisibility(isVisible)?.let { state ->
                            client.updateElement(element.updateElement(state))
                        }
                    }
                }
            }
        }
    }

    @Serializable
    @SerialName("MatchMetaGroupVisibility")
    data class MatchMetaGroupVisibility(
        override val publisherId: MetaId,
    ) : Match(), MetaPublisherAction {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta
        ) {
            meta.runIf<FilterGroupMeta> {
                filters.mapNotNull { client.metaMap[it.id] }
                    .mapNotNull { m ->
                        client.metaMap[(m as? FilterMeta)?.targetGroup]?.let { group ->
                            (group as? FilterMetaStore)?.let { listMeta ->
                                (listMeta.metas.isEmpty() || listMeta.metas.contains(m.id))
                            }
                        }
                    }.all {
                        it
                    }.let { isVisible ->
                        (element.state as? Visibility<out State>)?.copyVisibility(isVisible)?.let { state ->
                            client.updateElement(element.updateElement(state))
                        }
                    }
            }
        }
    }

    @Serializable
    @SerialName("ToggleFilterStoreMeta")
    data object ToggleFilterStoreMeta : Match() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta
        ) {
            meta.runIf<FilterMeta> {
                client.metaMap[targetGroup]?.let { group ->
                    (group as? FilterMetaStore)?.let { store ->
                        val updatedMetas = if (store.metas.contains(id)) {
                            store.metas.filter { it != id }
                        } else {
                            store.metas + id
                        }
                        group.copy(metas = updatedMetas)
                    }
                }?.also { updatedMeta ->
                    client.updateMeta(updatedMeta)
                }
            }
        }
    }

    /**
     * If all target elements are visible, then the subject element's visibility should also be visible.
     */
    @Serializable
    @SerialName("UpdateVisibility")
    data object MatchVisibility : Match() {
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
    data object MatchEnabled : Match() {
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
    data object MatchToggled : Match() {
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
    data object MatchLoading : Match() {
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
    data object MatchVariant : Match() {
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
        override val publisherIds: List<ElementId> = listOf(),
    ) : Match(), MultiElementPublisherAction {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            // TODO Now I think about it this doesn't actually initial run for the first time,
            // TODO this works in the context of when the default value is set correctly, but otherwise will only be correct after the first trigger
            // TODO I think I will probably need a flag to determine if the action should be triggered initially on screen load.
            // TODO Or we might just use another trigger for this to make things generic and clear
            val valid = publisherIds.mapNotNull { client.gElement(it).state as? Valid<out State> }
                .all { it.valid == true }
            (element.state as? Valid<out State>)?.copyValid(valid)?.let { state ->
                client.updateElement(element.updateElement(state))
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
    data object UpdateState : Action() {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            client.updateElement(element)
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
    data object SendState : Action() {
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
    @Suppress("UNCHECKED_CAST")
    data class UpdateValue(
        override val publisherId: ElementId,
    ) : Action(), ElementPublisherAction {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            val targetElement =
                client.gElement(publisherId).state as? Value<out State>
                    ?: error("Target element is not a value")
            val updatedElement = (element as Element<Value<out State>>).state.copyValue(targetElement.value)
            client.updateElement(element.updateElement(updatedElement))
        }
    }

    @Serializable
    @SerialName("ToggleVisibility")
    data class ToggleVisibility(
        override val targetId: ElementId,
    ) : Action(), ElementTargetAction {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            val targetElement = client.gElement(targetId)
            val updatedState = targetElement.state.copyVisibility(!targetElement.state.visible)
            val updatedElement = targetElement.updateElement(updatedState)
            client.updateElement(updatedElement)
        }
    }

    @Serializable
    @SerialName("ExecuteDeferred")
    data class ExecuteDeferred(
        override val publisherId: MetaId
    ) : Action(), MetaPublisherAction {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta,
        ) {
            client.deferredActions[publisherId]?.forEach {
                it.action.execute(
                    element = client.gElement(it.id),
                    client = client,
                    meta = client.metaMap[it.metaID] ?: None(),
                )
            }
            client.deferredActions.remove(publisherId)
        }
    }

    @Serializable
    @SerialName("ClearDeferred")
    data class ClearDeferred(
        override val publisherId: MetaId,
    ) : Action(), MetaPublisherAction {
        override fun execute(
            element: Element<out State>,
            client: Client,
            meta: Meta
        ) {
            client.deferredActions[publisherId]?.forEach {
                it.restoredElement?.let { restoredElement -> client.updateElement(restoredElement) }
            }
            client.deferredActions.remove(publisherId)
        }
    }
}

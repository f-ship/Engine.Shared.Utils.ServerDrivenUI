package ship.f.engine.shared.utils.serverdrivenui.client

import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig
import ship.f.engine.shared.utils.serverdrivenui.ext.fGet
import ship.f.engine.shared.utils.serverdrivenui.ext.measureInMillis
import ship.f.engine.shared.utils.serverdrivenui.state.State

interface Client {
    /**
     * The map of elements that the client keeps of track off
     */
    val elementMap: MutableMap<ScreenConfig.ID, ScreenConfig.Element<out State>>

    /**
     * When the server sends the client a component, and it can't render, the component will be rendered with fallback behavior.
     * Typically, a component will hide by default but may also show an optional update required banner.
     * In some cases if a feature is critical, a mandatory update required banner may be shown.
     */
    var banner: ScreenConfig.Fallback?

    /**
     * Responsible for first updating the state of the element and then propagating the state to all listeners
     */
    fun updateState(element: ScreenConfig.Element<out State>) = measureInMillis("updateState: ${element.id}") {
        elementMap[element.id] = element
        propagateState(element)
        postElementUpdateHook(element)
    }

    /**
     * Each listener depending on the action should be able to read the state of the element that propagated it's state
     * The reason the state is not passed directly is that the listener may be interested in multiple elements.
     * Even if one of the targets triggered the action, it may still need to decide on how to handle the state based on all targets
     */
    private fun propagateState(element: ScreenConfig.Element<out State>) {
        element.listeners.forEach { listener ->
            listener.action.execute(elementMap.fGet(listener.id), this)
        }
    }

    /**
     * This hook transforms the element map into a map of reactive elements which clients can bind to.
     * This is done so that the shared library does not need to depend on composed runtime directly, which should reduce its bundle size.
     * Since the shared library will be included in backend environments, it's imperative to keep the bundle lean to make cold starts fast.
     */
    fun postElementUpdateHook(element: ScreenConfig.Element<out State>)
}
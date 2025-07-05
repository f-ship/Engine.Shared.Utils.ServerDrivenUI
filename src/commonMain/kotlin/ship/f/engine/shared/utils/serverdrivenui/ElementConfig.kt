package ship.f.engine.shared.utils.serverdrivenui

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Element
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.ID
import ship.f.engine.shared.utils.serverdrivenui.state.State

@Serializable
data class ElementConfig (
    /**
     * Decides whether the element should be placed inside another element or if null on the current root screen config
     */
    val inside: ID,
    /**
     * Decides whether the element should be placed after another element or if null on the end of the children
     */
    val after: ID? = null,
    /**
     * The element to be placed on the screen
     */
    val elements: List<Element<out State>>,
)
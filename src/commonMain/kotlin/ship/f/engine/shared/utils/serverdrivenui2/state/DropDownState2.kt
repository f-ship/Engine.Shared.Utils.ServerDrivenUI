package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.PaddingValues2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.PaddingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2

@Serializable
@SerialName("DropDownState2")
data class DropDownState2(
    override val id: StateId2 = autoStateId2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = Size2.DefaultSize2,
    override val weight: Weight2? = null,
    override val metas: List<Meta2> = listOf(),
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val counter: Int = 0,
    val items: List<DropDownItem2>,
    val selectedItem: DropDownItem2? = null,
    val isExpanded: Boolean = false,
) : State2(), PaddingModifier2<DropDownState2> {
    override fun c(id: StateId2) = copy(id = id)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun cM(metas: List<Meta2>) = copy(metas = metas)

    @Serializable
    data class DropDownItem2(
        val id: String,
        val title: String,
    )
}
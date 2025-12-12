package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client3.Path3
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2.Color2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.ConditionalLiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.ConditionalValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.IntValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.PaddingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnBuildCompleteTrigger2
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
    @Deprecated("Use children instead")
    val items: List<DropDownItem2>? = null,
    val selectedItem: DropDownItem2? = null,
    val isExpanded: Boolean = false,
    override val path3: Path3 = Path3.Init,
    override val path: Path2 = Path2(),
    override val onBuildCompleteTrigger2: OnBuildCompleteTrigger2 = OnBuildCompleteTrigger2(),
    override val draws: List<Draw2> = listOf(),
    val title: String = "",
    val liveTitle: LiveValue3? = null,
    val liveSelectedTitle: LiveValue3? = null,
    val titleColor: Color2 = Color2.Unspecified,
    val titleTextStyle: TextStyle2 = TextStyle2.BodyMedium2,
    val menuColor2: Color2 = Color2.OnPrimary,
    val arrowDropDownIcon: ImageState2? = null,
    val arrowDropUpIcon: ImageState2? = null,
    override val children: List<State2> = listOf(),
    override val filteredChildren: List<State2>? = null,
    override val filter: List<ConditionalLiveValue2>? = null,
    override val filter3: ConditionalValue? = null,
    override val sort: LiveValue2? = null,
    override val sort3: LiveValue3? = null,
    override val focus: IntValue? = null,
    override val jumpTo: List<ConditionalLiveValue2>? = null,
    override val jumpTo3: ConditionalValue? = null,
) : State2(),
    PaddingModifier2<DropDownState2>,
    ChildrenModifier2<DropDownState2> {
    override fun c(id: StateId2) = copy(id = id)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun c(path3: Path3) = copy(path3 = path3)
    override fun c(path: Path2) = copy(path = path)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(children: List<State2>) = copy(children = children)
    override fun modifiedChildren(modifiedChildren: List<State2>?) = copy(filteredChildren = modifiedChildren)
    override fun filter(filter: List<ConditionalLiveValue2>?) = copy(filter = filter)
    override fun filter3(filter3: ConditionalValue?) = copy(filter3 = filter3)
    override fun c(sort: LiveValue2?) = copy(sort = sort)
    override fun c(sort3: LiveValue3?) = copy(sort3 = sort3)
    override fun jumpTo(jumpTo: List<ConditionalLiveValue2>?) = copy(jumpTo = jumpTo)
    override fun jumpTo3(jumpTo3: ConditionalValue?) = copy(jumpTo3 = jumpTo3)
    override fun focus(focus: IntValue?) = copy(focus = focus)

    @Serializable
    data class DropDownItem2(
        val id: String,
        val title: String,
    )
}
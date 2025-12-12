package ship.f.engine.shared.utils.serverdrivenui2.state

import ship.f.engine.shared.utils.serverdrivenui2.client3.Path3
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.ConditionalLiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Path2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.ConditionalValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.IntValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnBuildCompleteTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2

data class VariantState2(
    override val id: StateId2 = autoStateId2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val children: List<State2> = emptyList(),
    override val weight: Weight2? = null,
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    override val draws: List<Draw2> = listOf(),
    override val onBuildCompleteTrigger2: OnBuildCompleteTrigger2 = OnBuildCompleteTrigger2(),
    override val path: Path2 = Path2(),
    override val path3: Path3 = Path3.Init,
    override val filteredChildren: List<State2>? = null,
    override val filter: List<ConditionalLiveValue2>? = null,
    override val filter3: ConditionalValue? = null,
    override val sort: LiveValue2? = null,
    override val sort3: LiveValue3? = null,
    override val focus: IntValue? = null,
    override val jumpTo: List<ConditionalLiveValue2>? = null,
    override val jumpTo3: ConditionalValue? = null,
    val variant: LiveValue3,
    val defaultVariant: LiveValue3,
) : State2(),
    ChildrenModifier2<VariantState2> {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(children: List<State2>) = copy(children = children)
    override fun modifiedChildren(modifiedChildren: List<State2>?) = copy(filteredChildren = modifiedChildren)
    override fun filter(filter: List<ConditionalLiveValue2>?) = copy(filter = filter)
    override fun filter3(filter3: ConditionalValue?) = copy(filter3 = filter3)
    override fun c(sort: LiveValue2?) = copy(sort = sort)
    override fun c(sort3: LiveValue3?) = copy(sort3 = sort3)
    override fun jumpTo(jumpTo: List<ConditionalLiveValue2>?) = copy(jumpTo = jumpTo)
    override fun jumpTo3(jumpTo3: ConditionalValue?) = copy(jumpTo3 = jumpTo3)
    override fun focus(focus: IntValue?) = copy(focus = focus)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun c(path3: Path3) = copy(path3 = path3)
    override fun c(path: Path2) = copy(path = path)
}
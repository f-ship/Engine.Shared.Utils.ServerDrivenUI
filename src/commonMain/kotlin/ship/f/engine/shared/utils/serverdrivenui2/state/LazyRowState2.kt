package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client3.Path3
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Alignment2.VerticalAlignment2.CenterVertically2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Arrangement2.VerticalArrangement2.Top2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Border2.Companion.default
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Color2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Color2.Unspecified
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2.Companion.DefaultShapes2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2.CornerBasedShape2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.ConditionalValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.IntValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnBuildCompleteTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2

@Serializable
@SerialName("LazyRowState2")
data class LazyRowState2(
    override val id: StateId2 = autoStateId2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val innerPadding: PaddingValues2 = PaddingValues2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val children: List<State2> = emptyList(),
    override val alignment: Alignment2 = CenterVertically2,
    override val arrangement: Arrangement2 = Top2,
    override val border: Border2 = default,
    override val color: Color2 = Unspecified,
    override val liveColor: ConditionalValue? = null,
    override val shape: CornerBasedShape2 = DefaultShapes2.small,
    override val weight: Weight2? = null,
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    override val path3: Path3 = Path3.Init,
    override val path: Path2 = Path2(),
    override val onBuildCompleteTrigger2: OnBuildCompleteTrigger2 = OnBuildCompleteTrigger2(),
    override val draws: List<Draw2> = listOf(),
    override val filteredChildren: List<State2>? = null,
    override val filter3: ConditionalValue? = null,
    override val sort3: LiveValue3? = null,
    override val focus: IntValue? = null,
    override val jumpTo3: ConditionalValue? = null,
) : State2(),
    ChildrenModifier2<LazyRowState2>,
    AlignmentModifier2<LazyRowState2>,
    ArrangementModifier2<LazyRowState2>,
    ColorModifier2<LazyRowState2>,
    ShapeModifier2<LazyRowState2>,
    InnerPaddingModifier2<LazyRowState2>,
    BorderModifier2<LazyRowState2> {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(children: List<State2>) = copy(children = children)
    override fun c(alignment: Alignment2) = copy(alignment = alignment)
    override fun c(arrangement: Arrangement2) = copy(arrangement = arrangement)
    override fun c(color: Color2) = copy(color = color)
    override fun c(shape: CornerBasedShape2) = copy(shape = shape)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun c(path3: Path3) = copy(path3 = path3)
    override fun c(path: Path2) = copy(path = path)
    override fun c(border: Border2) = copy(border = border)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun modifiedChildren(modifiedChildren: List<State2>?) = copy(filteredChildren = modifiedChildren)
    override fun filter3(filter3: ConditionalValue?) = copy(filter3 = filter3)
    override fun c(sort3: LiveValue3?) = copy(sort3 = sort3)
    override fun jumpTo3(jumpTo3: ConditionalValue?) = copy(jumpTo3 = jumpTo3)
    override fun focus(focus: IntValue?) = copy(focus = focus)
    override fun liveColor(liveColor: ConditionalValue) = copy(liveColor = liveColor)

    override fun reset(counter: Int) = copy(counter = counter)
    override fun c(
        padding: PaddingValues2,
        innerPadding: PaddingValues2,
    ) = copy(
        padding = padding,
        innerPadding = innerPadding
    )
}
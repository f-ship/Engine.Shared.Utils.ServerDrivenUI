package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2.Companion.DefaultDarkColorScheme2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2.Companion.DefaultLightColorScheme2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2.Companion.DefaultShapes2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.Window2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2

@Serializable
@SerialName("ScaffoldState2")
data class ScaffoldState2(
    override val id: StateId2 = autoStateId2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val innerPadding: PaddingValues2 = PaddingValues2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = Window2,
    override val alignment: Alignment2 = Alignment2.HorizontalAlignment2.CenterHorizontally2,
    override val arrangement: Arrangement2 = Arrangement2.VerticalArrangement2.Top2,
    override val children: List<State2> = listOf(),
    override val lightColorScheme: ColorScheme2 = DefaultLightColorScheme2,
    override val darkColorScheme: ColorScheme2 = DefaultDarkColorScheme2,
    override val shapes: Shapes2 = DefaultShapes2,
    override val font: String = "InterVariable",
    override val weight: Weight2? = null,
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    override val draws: List<Draw2> = listOf(),
    override val color: ColorScheme2.Color2 = ColorScheme2.Color2.OnPrimary,
    val header: State2? = null,
    val bottom: State2? = null,
) : State2(),
    ChildrenModifier2<ScaffoldState2>,
    ColorSchemeModifier2<ScaffoldState2>,
    ColorModifier2<ScaffoldState2>,
    ShapesModifier2<ScaffoldState2>,
    TypographyModifier2<ScaffoldState2>,
    InnerPaddingModifier2<ScaffoldState2>,
    AlignmentModifier2<ScaffoldState2>,
    ArrangementModifier2<ScaffoldState2> {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(arrangement: Arrangement2) = copy(arrangement = arrangement)
    override fun c(children: List<State2>) = copy(children = children)
    override fun c(shapes: Shapes2) = copy(shapes = shapes)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun c(font: String) = copy(font = font)
    override fun c(alignment: Alignment2) = copy(alignment = alignment)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun c(
        lightColorScheme: ColorScheme2,
        darkColorScheme: ColorScheme2,
    ) = copy(
        lightColorScheme = lightColorScheme,
        darkColorScheme = darkColorScheme,
    )

    override fun c(
        padding: PaddingValues2,
        innerPadding: PaddingValues2,
    ) = copy(
        padding = padding,
        innerPadding = innerPadding,
    )
    override fun c(color: ColorScheme2.Color2) = copy(color = color)
}

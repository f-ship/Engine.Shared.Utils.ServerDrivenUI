package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.modifiers.MetaIdModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Path2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2

@Serializable
@SerialName("BuilderState2")
data class BuilderState2(
    override val id: StateId2 = autoStateId2(),
    override val metaId: MetaId2 = MetaId2("json-meta"),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val weight: WeightModifier2.Weight2? = null,
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val draws: List<Draw2> = listOf(),
    override val path: Path2 = Path2(),
) : State2(), MetaIdModifier2 {
    override fun c(id: StateId2) = copy(id = id)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(weight: WeightModifier2.Weight2) = copy(weight = weight)
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun c(path: Path2) = copy(path = path)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
}
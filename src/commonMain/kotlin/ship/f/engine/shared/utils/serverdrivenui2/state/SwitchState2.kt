package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.PaddingValues2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Path2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.PaddingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2.Valid2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnBuildCompleteTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnToggleTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnToggleModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnToggleModifier2.Toggle2

@Serializable
@SerialName("SwitchState2")
data class SwitchState2(
    override val id: StateId2 = autoStateId2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val valid: Valid2 = Valid2(true),
    override val toggle: Toggle2 = Toggle2(false),
    override val modified: Boolean = false,
    override val initialToggle: Boolean? = false,
    override val weight: Weight2? = null,
    override val onToggleTrigger: OnToggleTrigger2 = OnToggleTrigger2(),
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    override val path: Path2 = Path2(),
    override val onBuildCompleteTrigger2: OnBuildCompleteTrigger2 = OnBuildCompleteTrigger2(),
    override val draws: List<Draw2> = listOf(),
) : State2(),
    PaddingModifier2<SwitchState2>,
    ValidModifier2<SwitchState2>,
    OnToggleModifier2<SwitchState2> {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(valid: Valid2) = copy(valid = valid)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun c(path: Path2) = copy(path = path)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun update(
        toggle: Toggle2,
        modified: Boolean,
        initialValue: Boolean,
    ) = copy(
        toggle = toggle,
        modified = modified,
        initialToggle = initialValue,
    )
}
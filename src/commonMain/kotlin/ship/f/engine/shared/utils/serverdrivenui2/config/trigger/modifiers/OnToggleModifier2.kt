package ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnToggleTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import kotlin.jvm.JvmInline

interface OnToggleModifier2<S : State2> : TriggerModifier2 {
    val onToggleTrigger: OnToggleTrigger2
    val toggle: Toggle2

    val liveToggle: LiveValue3
    val modified: Boolean
    val initialToggle: Boolean?
    fun update(toggle: Toggle2, modified: Boolean, initialValue: Boolean): S

    @Serializable
    @JvmInline
    value class Toggle2(val value: Boolean)
}
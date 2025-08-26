package ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnStateUpdateTrigger2

interface OnStateUpdateModifier2 : TriggerModifier2 {
    val onStateUpdateTrigger: OnStateUpdateTrigger2
}
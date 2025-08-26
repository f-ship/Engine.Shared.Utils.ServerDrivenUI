package ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnFieldUpdateTrigger2

interface OnFieldUpdateModifier2 : TriggerModifier2 {
    val onFieldUpdateTrigger: OnFieldUpdateTrigger2
}
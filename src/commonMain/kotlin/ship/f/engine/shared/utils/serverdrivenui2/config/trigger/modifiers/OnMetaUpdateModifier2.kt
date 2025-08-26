package ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnMetaUpdateTrigger2

interface OnMetaUpdateModifier2 : TriggerModifier2 {
    val onMetaUpdateTrigger: OnMetaUpdateTrigger2
}
package ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnClickTrigger2

interface OnClickModifier2 : TriggerModifier2 {
    val onClickTrigger: OnClickTrigger2
}
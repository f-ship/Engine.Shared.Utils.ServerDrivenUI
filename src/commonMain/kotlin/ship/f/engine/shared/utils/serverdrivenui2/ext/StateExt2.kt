package ship.f.engine.shared.utils.serverdrivenui2.ext

import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.PopulatedSideEffectMeta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.state.*
fun PopulatedSideEffectMeta2.getText(id: Id2.StateId2) = when (val state = states.first { it.id == id }) {
    is ButtonState2 -> state.text
    is DropDownState2 -> state.selectedItem?.title
    is TextFieldState2 -> state.text
    is SearchState2 -> state.text
    is TextState2 -> state.text
    else -> null
}

fun PopulatedSideEffectMeta2.getTextByAlias(alias: String) = when (val state = states.first { it.id.alias == alias }) {
    is ButtonState2 -> state.text
    is DropDownState2 -> state.selectedItem?.title
    is TextFieldState2 -> state.text
    is SearchState2 -> state.text
    is TextState2 -> state.text
    else -> null
}

fun PopulatedSideEffectMeta2.getBoolean(id: Id2.StateId2) = when (val state = states.first { it.id == id }) {
    is CheckboxState2 -> state.toggle.value
    else -> null
}

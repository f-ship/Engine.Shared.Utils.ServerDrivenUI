package ship.f.engine.shared.utils.serverdrivenui2.ext

import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.PopulatedSideEffectMeta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
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

inline fun <reified T : State2> State2.find(id: Id2.StateId2): T {
    val toCheck = mutableListOf(this)
    for (i in toCheck) {
        if (i.id == id) return i as T
        else if (i is ChildrenModifier2<*>) toCheck.addAll(i.children)
    }
    error("Did not find state in find for $id")
}

inline fun <reified T : State2> State2.find(id: String): T {
    val toCheck = mutableListOf(this)
    val iterator = toCheck.listIterator()
    var hasNext = iterator.hasNext()
    while (hasNext) {
        val item = iterator.next()
        sduiLog( item.id.name, tag = "find > while")
        if (item.id.name == id) return item as T
        else if (item is ChildrenModifier2<*>) item.children.forEach {
            sduiLog( it.id.name, tag = "find > while > children.forEach")
            iterator.add(it)
            iterator.previous()
        }
        sduiLog( item.id.name, iterator.hasNext(), tag = "find > while > hasNext")
    }
    error("Did not find state in find for $id")
}

package ship.f.engine.shared.utils.serverdrivenui2.client3

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ship.f.engine.shared.utils.serverdrivenui2.client.BackStackEntry2.Direction2.Backward2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.InsertionOperation2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

class NavigationEngine(val client: Client3) {
    val backstack: MutableList<BackStackEntry3> = mutableListOf()
    val currentScreen: MutableState<BackStackEntry3?> = mutableStateOf(null)

    fun navigate(operation: NavigationConfig2.StateOperation2) {
        when(operation) {
            is NavigationConfig2.StateOperation2.InsertionOperation2 -> {
                val inside = client.get<State2>(operation.inside)
                val parent = inside as? ChildrenModifier2<*>
                    ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.inside}")
                val child = client.get<State2>(operation.stateId)
                client.initState(child, inside.path3.toRenderChain())

                val updatedParent = when(operation){
                    is Start2 -> parent.c(listOf(child) + parent.children)
                    is End2 -> parent.c(parent.children + child)
                    is Before2 -> parent.c(insert(parent.children, child, operation.before, operation.offset))
                    is After2 -> parent.c(insert(parent.children, child, operation.after, operation.offset))
                    is Remove2 -> parent.c(parent.children.filter { child -> child.id != operation.stateId })
                }

                client.update(updatedParent)
            }

            is NavigationConfig2.StateOperation2.Push2 -> {
                val state = client.get<State2>(operation.stateId)
                val entry = BackStackEntry3.ScreenEntry(state)
                backstack.add(entry)
                client.commit() // Need to commit before setting it to the current screen
                currentScreen.value = entry
            }

            is NavigationConfig2.StateOperation2.ReplaceChild2 -> {
                val inside = client.get<State2>(operation.container)
                val parent = inside as? ChildrenModifier2<*>
                    ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.container}")
                val child = client.get<State2>(operation.stateId)
                val updatedChild = client.initState(child, inside.path3.toRenderChain())
                val updatedParent = parent.c(listOf(updatedChild))
                client.update(updatedParent)
            }

            is NavigationConfig2.StateOperation2.Swap2 -> {
                val originalState = client.get<State2>(operation.swap.id)
                val renderingChain = originalState.path3.toRenderChain().run { subList(0, lastIndex) }
                val updatedState = client.buildPaths(operation.swap, renderingChain).also { client.setPaths(it) }
                client.update(updatedState)
            }
            else -> Unit
        }
        client.commit()
    }

    fun canPop() = backstack.isNotEmpty() && backstack.subList(0, backstack.lastIndex).any { it.canPopBack }
    fun pop() {
        backstack.removeLast()
        backstack.dropLastWhile { !it.canPopBack }
        when(val entry = backstack.last()) {
            is BackStackEntry3.ScreenEntry -> currentScreen.value = entry.copy(direction2 = Backward2) // TODO wrap in an self destructing animation
            is BackStackEntry3.ViewEntry -> navigate(
                NavigationConfig2.StateOperation2.ReplaceChild2(
                    stateId = entry.stateId,
                    container = entry.containerId
                )
            )
        }
    }

    private fun insert(
        existing: List<State2>,
        addition: State2,
        insertion: Id2.StateId2,
        offset: Int = 0,
    ) = existing
        .indexOfFirst { it.id == insertion }
        .let { index ->
            if (index == -1) {
                existing + addition
            } else {
                existing.subList(0, index + offset) + addition + existing.subList(index + offset, existing.size)
            }
        }
}
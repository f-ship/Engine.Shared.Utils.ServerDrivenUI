package ship.f.engine.shared.utils.serverdrivenui2.client

import ship.f.engine.shared.utils.serverdrivenui2.state.State2

data class BackStackEntry2(
    val direction: Direction2,
    val state: State2,
    val canPopBack: Boolean = true,
) {
    sealed class Direction2 {
        data object Forward2 : Direction2()
        data object Backward2 : Direction2()
    }
}
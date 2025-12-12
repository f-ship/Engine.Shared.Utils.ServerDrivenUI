package ship.f.engine.shared.utils.serverdrivenui2.client3

import ship.f.engine.shared.utils.serverdrivenui2.client.BackStackEntry2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.RemoteAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

sealed class BackStackEntry3 {
    abstract val canPopBack: Boolean
    data class ScreenEntry(
        val state2: State2,
        val direction2: BackStackEntry2.Direction2 = BackStackEntry2.Direction2.Forward2, // TODO to remove, just keeping for now for backwards compatibility
        override val canPopBack: Boolean = true,
    ): BackStackEntry3()

    data class ViewEntry(
        val containerId: Id2.StateId2,
        val stateId: Id2.StateId2,
        val remoteActions: List<RemoteAction2<Action2>> = emptyList(), // TODO will be used to update ZoneModels etc
        val groupKey: String? = null,
        override val canPopBack: Boolean = true,
    ) : BackStackEntry3()

}
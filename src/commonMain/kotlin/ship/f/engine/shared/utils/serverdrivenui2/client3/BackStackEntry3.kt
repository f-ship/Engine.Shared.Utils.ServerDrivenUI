package ship.f.engine.shared.utils.serverdrivenui2.client3

import ship.f.engine.shared.utils.serverdrivenui2.client.BackStackEntry2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.RemoteAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.PushState2.SavedZone
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

sealed class BackStackEntry3 {
    abstract val canPopBack: Boolean
    abstract val savedZones: List<SavedZone>
    abstract val refreshStates: List<StateId2>
    data class ScreenEntry(
        val state2: State2,
        val direction2: BackStackEntry2.Direction2 = BackStackEntry2.Direction2.Forward2, // TODO to remove, just keeping for now for backwards compatibility
        override val canPopBack: Boolean = true,
        override val savedZones: List<SavedZone> = emptyList(),
        override val refreshStates: List<StateId2> = emptyList(),
    ): BackStackEntry3()

    data class ViewEntry(
        val containerId: StateId2,
        val stateId: StateId2,
        val remoteActions: List<RemoteAction2<Action2>> = emptyList(), // TODO will be used to update ZoneModels etc
        val groupKey: String? = null,
        val restoreContainer: StateId2,
        val restoreState: StateId2,
        override val canPopBack: Boolean = true,
        override val savedZones: List<SavedZone> = emptyList(),
        override val refreshStates: List<StateId2> = emptyList(), // TODO while we don't have reactive zone models
    ) : BackStackEntry3()

}
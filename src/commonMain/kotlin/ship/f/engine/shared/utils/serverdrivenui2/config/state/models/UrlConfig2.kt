package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Navigate2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2

@Serializable
data class UrlConfig2(
    val url: String,
    val whitelist: List<Navigation>
) {
    @Serializable
    data class Navigation(
        val url: String, // Determines if a url is accepted to be navigated to
        val params: List<String> = listOf(), // Determines which parameters should be extracted and sent as a side effect
        val metaId: Id2.MetaId2 = autoMetaId2(),
        val destination: Navigate2? = null, // Determines if the WebView should navigate away
    )
}
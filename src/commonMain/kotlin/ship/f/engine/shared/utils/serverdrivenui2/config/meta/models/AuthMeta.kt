package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2

@Serializable
data class AuthMeta2(
    override val metaId: Id2.MetaId2 = autoMetaId2(),
    val auth: PasswordAuth2,
) : Meta2() {
    @Serializable
    data class PasswordAuth2(
        val userId: String,
        val accessToken: String,
        val refreshToken: String,
    )
}
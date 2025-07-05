package ship.f.engine.shared.utils.serverdrivenui.client

/**
 * Used as a shared library analogy to the CompositionLocalProvider
 */
object ClientHolder {
    private val clients = mutableListOf<Client>()
    fun addClient(client: Client) { clients.add(client) }
    fun getClient() = clients.last()
}
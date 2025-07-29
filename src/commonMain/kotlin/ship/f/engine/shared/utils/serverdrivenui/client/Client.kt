package ship.f.engine.shared.utils.serverdrivenui.client

import ship.f.engine.shared.utils.serverdrivenui.ElementConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*
import ship.f.engine.shared.utils.serverdrivenui.action.*
import ship.f.engine.shared.utils.serverdrivenui.action.Trigger.OnInitialRenderTrigger
import ship.f.engine.shared.utils.serverdrivenui.ext.fGet
import ship.f.engine.shared.utils.serverdrivenui.ext.measureInMillis
import ship.f.engine.shared.utils.serverdrivenui.state.ComponentState
import ship.f.engine.shared.utils.serverdrivenui.state.State
import ship.f.engine.shared.utils.serverdrivenui.state.WidgetState

abstract class Client {
    /**
     * The map of elements that the client keeps of track off
     */
    val elementMap: MutableMap<ElementId, Element<out State>> = mutableMapOf()

    /**
     * Map of screenConfigs that have been properly initialized
     */
    val screenConfigMap: MutableMap<ElementId, ScreenConfig> = mutableMapOf()

    /**
     * Backstack of screenConfigs that have been visited
     */
    var backstack: MutableList<ScreenConfig> = mutableListOf()

    /**
     * Map used as session storage for context that may be required by the server but not important for the client
     */
    val metaMap: MutableMap<MetaId, Meta> = mutableMapOf()

    /**
     * Map used to store listeners for meta-updates, this is pretty ugly for now, should probably fo
     */
    val metaListenerMap: MutableMap<MetaId, MutableList<RemoteAction>> = mutableMapOf()

    /**
     * Deferred Actions can be executed or canceled at a later time, this map cache said actions
     * I probably should at some point replace all these maps and things with a Meta Implementation
     */
    val deferredActions: MutableMap<MetaId, MutableList<DeferredAction>> = mutableMapOf()

    /**
     * When the server sends the client a component, and it can't render, the component will be rendered with fallback behavior.
     * Typically, a component will hide by default but may also show an optional update required banner.
     * In some cases if a feature is critical, a mandatory update required banner may be shown.
     */
    var banner: Fallback? = null

    /**
     * Push a new screenConfig onto the backstack and set the state of the screenConfig.
     * If the last screen in the backstack has the same ID as the new screenConfig,
     * then the screenConfig will be removed from the backstack as it will be replaced by the new screenConfig.
     */
    fun navigate(config: ScreenConfig) {
        if (backstack.lastOrNull()?.id == config.id) {
            backstack.removeLast()
            screenConfigMap.remove(config.id)
        }

        if (screenConfigMap[config.id] == null) {
            config.children.forEach {
                setState(it)
                setTriggers(it)
                initialTriggers(it)
            }
            screenConfigMap[config.id] = config
        }

        addConfig(config)
        postScreenConfig()
    }

    /**
     * Add elements to a screenConfig
     */
    fun navigate(config: ElementConfig) { // TODO such a disgusting method

        if (config.replace != null) {
            val replaceElement = config.elements.first()
            elementMap[config.replace] = replaceElement
            postReactiveUpdate(replaceElement)
            return // TODO we should probably utilise some more early returns in this function
        }

        /**
         * Used to make all new elements that are being added to become reactive
         */
        config.elements.forEach {
            setState(it)
            setTriggers(it)
            initialTriggers(it)
        }



        /**
         * If the screenConfig exists, then update the children of the screenConfig.
         */
        val possibleScreenConfig = screenConfigMap[config.inside]
        if (possibleScreenConfig != null) {
            val updatedChildren = combineChildren(possibleScreenConfig.children, config)
            val updatedConfig = possibleScreenConfig.copy(children = updatedChildren)

            screenConfigMap[config.inside] = updatedConfig
            backstack = backstack.map {
                if (it.id == config.inside) {
                    updatedConfig
                } else {
                    it
                }
            }.toMutableList()
            postScreenConfig()
        } else {
            /**
             * If not being added to the route of a screen config then add as a child to an element
             */
            val element = elementMap.fGet(config.inside)

            when (element) {
                is Component<out ComponentState> -> Unit
                is Widget<out WidgetState> -> {
                    val updatedChildren = combineChildren(element.state.children, config)
                    val updatedState = element.state.copyChildren(children = updatedChildren)
                    val updatedElement = element.updateElement(state = updatedState)
                    elementMap[element.id] = updatedElement
                    postReactiveUpdate(updatedElement)
                }
            }
        }
    }

    /**
     * Initially generates the element map for the client
     */
    fun setState(element: Element<out State>) {
        if (elementMap[element.id] != null && elementMap[element.id] != element) {
            // TODO need a smarter way of handling the detection of duplicate IDs, I think this is where composite IDs should be useful
//            error("Duplicate ID: ${element.id}")
            println("Duplicate ID: ${element.id}")
        }
        elementMap[element.id] = element
        element.metas.forEach { metaMap[it.key] = it.value }
        createReactiveUpdate(element)

        when (element) {
            is Component<*> -> Unit
            is Widget<*> -> element.state.children.forEach { setState(it) }
        }
    }

    /**
     * Used to recursively add triggers to the client's element map
     */
    fun setTriggers(element: Element<out State>) {
        element.triggers.filterIsInstance<Trigger.OnStateUpdateTrigger>().forEach {
            when(it.action){
                is ElementTargetAction -> setTrigger(element, it.action.targetId, it.action)
                is MultiElementTargetAction -> it.action.targetIds.forEach { target -> setTrigger(element, target, it.action) }
                else -> Unit
            }
        }

        element.triggers.filterIsInstance<Trigger.OnMetaUpdateTrigger>().forEach {
            when(it.action){
                is MetaTargetAction -> setMetaTrigger(element, it.action.targetId, it.action, it.metaID)
                else -> Unit
            }
        }

        when (element) {
            is Component<*> -> Unit
            is Widget<*> -> element.state.children.forEach { setTriggers(it) }
        }
    }

    private fun setTrigger(element: Element<out State>, target: ElementId, action: Action) {
        val targetElement = elementMap.fGet(target)
        val updatedElement = targetElement.updateElement(
            listeners = targetElement.listeners + RemoteAction(
                action = action,
                id = element.id,
            )
        )

        elementMap[target] = updatedElement
        createReactiveUpdate(updatedElement)
    }

    private fun setMetaTrigger(element: Element<out State>, target: MetaId, action: Action, metaId: MetaId) {
        if (metaListenerMap[target] == null) {
            metaListenerMap[target] = mutableListOf()
        }

        metaListenerMap[target]!!.add(
            RemoteAction(
                action = action,
                id = element.id,
                metaID = metaId,
            )
        )
    }

    fun initialTriggers(element: Element<out State>) {
        element.triggers.filterIsInstance<OnInitialRenderTrigger>().forEach {
            it.action.execute(
                element = element,
                client = this,
                meta = metaMap[it.metaID] ?: Meta.None(),
            )
        }

        when (element) {
            is Component<*> -> Unit
            is Widget<*> -> element.state.children.forEach { initialTriggers(it) }
        }
    }

    // TODO to be used in the action that toggles the existence of a value in a MetaList
    fun updateMeta(meta: Meta) {
        metaMap[meta.id] = meta

        // TODO when a meta updates we want to run the correct action with the correct element
        metaListenerMap[meta.id]?.forEach {
            it.action.execute(
                element = elementMap.fGet(it.id),
                client = this,
                meta = metaMap[it.metaID] ?: Meta.None(), // TODO a little dangerous just adding a default value without propper logging, benchmarking and logging will come soon
            )
        }
    }

    /**
     * Responsible for first updating the state of the element and then propagating the state to all listeners
     */
    fun updateState(element: Element<out State>) = measureInMillis("updateState: ${element.id}") {
        elementMap[element.id] = element
        propagateState(element)
        postReactiveUpdate(element)
    }

    private fun combineChildren(children: List<Element<out State>>, config: ElementConfig) = if (config.after != null) {
        children
            .indexOfFirst { it.id == config.after }
            .let {
                if (it == -1) {
                    children + config.elements
                } else {
                    children.subList(
                        0,
                        it + 1
                    ) + config.elements + children.subList(
                        it + 1,
                        children.size
                    )
                }
            }
    } else {
        children + config.elements
    }


    /**
     * Add a screenConfig to the backstack
     */
    private fun addConfig(config: ScreenConfig) {
        backstack.add(config)
        postScreenConfig()
    }

    /**
     * Each listener depending on the action should be able to read the state of the element that propagated it's state
     * The reason the state is not passed directly is that the listener may be interested in multiple elements.
     * Even if one of the targets triggered the action, it may still need to decide on how to handle the state based on all targets
     */
    private fun propagateState(element: Element<out State>) {
        element.listeners.forEach { listener ->
            listener.action.execute(elementMap.fGet(listener.id), this)
        }
    }

    /**
     * This hook transforms the element map into a map of reactive elements which clients can bind to.
     * This is done so that the shared library does not need to depend on composed runtime directly, which should reduce its bundle size.
     * Since the shared library will be included in backend environments, it's imperative to keep the bundle lean to make cold starts fast.
     */
    abstract fun postReactiveUpdate(element: Element<out State>)

    /**
     * Used to enable Client to handle setting state while enabling downstream clients to listen to state changes.
     */
    abstract fun createReactiveUpdate(element: Element<out State>)

    /**
     * Refreshes the last screen in the backstack
     */
    abstract fun postScreenConfig()
}
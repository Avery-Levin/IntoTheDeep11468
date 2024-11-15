package org.baylorschool.intothedeep

/**
 * This is a simple action framework.
 * ActionGroups execute things concurrently until everything in the group is done.
 * ActionSets execute things one at a time and the "execute()" function is blocking.
 * init() and update() should be non-blocking
 */
interface Action {
    fun init()
    /**
     * @returns whether the action is done or not
     */
    fun update() : Boolean
}
class ActionGroup(vararg actions0: Action) : Action {
    private val actions = actions0.toMutableList()
    override fun init() {
        for (i in actions) {
            i.init()
        }
    }
    override fun update(): Boolean {
        for (i in actions) {
            actions.removeIf { i.update() }
        }
        return actions.isEmpty()
    }
}
class ActionSet(vararg actions0: Action) {
    private val actions = actions0.toMutableList()
    /**
     * this is blocking, obviously.
     */
    fun execute() {
        for (i in actions) {
            i.init()
            while (!i.update()) {
                // do nothing
            }
        }
    }
}
fun initAction(id: () -> Unit): Action {
    return object : Action {
        override fun init() {
            id()
        }
        override fun update(): Boolean = true
    }
}
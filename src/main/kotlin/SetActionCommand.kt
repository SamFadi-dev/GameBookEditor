package gamebook.editor

class SetActionCommand(
    private val node: Node,
    private val index: Int,
    private val newAction: Action
) : Command {
    private var oldAction: Action? = null
    private var addedNew = false

    override fun execute() {
        if (index < node.actions.size) {
            oldAction = node.actions[index]
            node.actions[index] = newAction
        } else {
            node.actions.add(newAction)
            addedNew = true
        }
    }

    override fun undo() {
        if (addedNew) {
            node.actions.removeLast()
        } else if (oldAction != null) {
            node.actions[index] = oldAction!!
        }
    }

    override val description = "Set action at index $index in node ${node.id}"
}

package gamebook.editor

class SetTextCommand(
    private val node: Node,
    private val newText: List<String>
) : Command {
    private val oldText = node.text.toList()

    override fun execute() {
        node.text.clear()
        node.text.addAll(newText)
    }

    override fun undo() {
        node.text.clear()
        node.text.addAll(oldText)
    }

    override val description = "Set text of node ${node.id}"
}

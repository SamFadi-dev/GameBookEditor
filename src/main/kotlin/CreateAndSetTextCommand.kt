package gamebook.editor

class CreateAndSetTextCommand(
    private val story: Story,
    private val id: Int,
    private val newText: List<String>
) : Command {

    private var nodeCreated = false
    private var oldText: List<String>? = null

    override fun execute() {
        val node = story.nodes.getOrPut(id) {
            nodeCreated = true
            Node(id)
        }

        oldText = node.text.toList()
        node.text.clear()
        node.text.addAll(newText)
    }

    override fun undo() {
        val node = story.nodes[id] ?: return
        if (nodeCreated) {
            story.nodes.remove(id)
        } else {
            node.text.clear()
            oldText?.let { node.text.addAll(it) }
        }
    }

    override val description = "Create/set text for node $id"
}

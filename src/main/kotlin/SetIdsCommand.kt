package gamebook.editor

class SetIdsCommand(
    private val story: Story,
    private val from: Int,
    private val to: Int,
    private val diff: Int
) : Command {

    private val oldNodes = mutableMapOf<Int, Node>()
    private val newNodes = mutableMapOf<Int, Node>()
    private val oldStartId = story.startNodeId
    private var newStartId = oldStartId
    private val updatedActions = mutableListOf<Pair<Action, Int>>() // (action, oldTargetId)

    override fun execute() {
        val range = (from..to)
        val affected = range.mapNotNull { story.nodes[it] }

        // Check conflicts
        val targetIds = affected.map { it.id + diff }
        if (targetIds.any { it in story.nodes && it !in range }) {
            throw IllegalStateException("ID conflict detected: target ID already exists.")
        }

        // Save older
        for (node in affected) {
            oldNodes[node.id] = node
            val newId = node.id + diff
            val newNode = node.copy(id = newId)
            newNodes[newId] = newNode
        }

        // Apply modification
        for (id in range) {
            story.nodes.remove(id)
        }

        // Update IDs of all nodes
        val allNodes = story.nodes.values + newNodes.values
        for (node in allNodes) {
            for (action in node.actions) {
                if (action.targetId in from..to) {
                    updatedActions.add(action to action.targetId)
                    action.targetId += diff
                }
            }
        }

        if (oldStartId in from..to) {
            newStartId = oldStartId + diff
            story.startNodeId = newStartId
        }

        story.nodes.putAll(newNodes)
    }

    override fun undo() {
        for (id in newNodes.keys) {
            story.nodes.remove(id)
        }

        for ((action, oldId) in updatedActions) {
            action.targetId = oldId
        }
        story.startNodeId = oldStartId
        story.nodes.putAll(oldNodes)
    }

    override val description = "Change node IDs from $from to $to by $diff"
}

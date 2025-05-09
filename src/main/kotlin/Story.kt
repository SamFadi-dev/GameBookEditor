package gamebook.editor

data class Story(
    var title: String = "",
    var startNodeId: Int = -1,
    val nodes: MutableMap<Int, Node> = mutableMapOf()
) {
    fun toMiniTwee(): String {
        val builder = StringBuilder()
        builder.appendLine(":: StoryTitle")
        builder.appendLine(title)
        builder.appendLine()
        builder.appendLine(":: StoryData")
        builder.appendLine("""{ "start": $startNodeId }""")
        builder.appendLine()

        nodes.toSortedMap().forEach { (id, node) ->
            builder.appendLine(":: $id")
            node.text.forEach { builder.appendLine(it) }
            node.actions.forEach { action ->
                builder.appendLine("[[${action.label}->${action.targetId}]]")
            }
            builder.appendLine()
        }

        return builder.toString()
    }

    fun storyCopy(): Story {
        val copy = Story(
            title = this.title,
            startNodeId = this.startNodeId,
            nodes = this.nodes.mapValues { (_, node) ->
                Node(
                    id = node.id,
                    text = node.text.toMutableList(),
                    actions = node.actions.map { it.copy() }.toMutableList()
                )
            }.toMutableMap()
        )
        return copy
    }

}
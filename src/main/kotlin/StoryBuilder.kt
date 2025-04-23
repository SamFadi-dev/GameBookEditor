package gamebook.editor

class StoryBuilder {
    private val story = Story()
    private var currentNode: Node? = null
    private var mode = ""

    fun processLine(line: String) {
        when {
            line.startsWith(":: StoryTitle") -> mode = "title"
            line.startsWith(":: StoryData") -> mode = "data"
            line.startsWith(":: ") -> {
                val id = line.removePrefix(":: ").trim().toInt()
                currentNode = Node(id)
                story.nodes[id] = currentNode!!
                mode = "node"
            }
            line.startsWith("[[") -> {
                val parts = line.removePrefix("[[").removeSuffix("]]").split("->")
                val label = parts[0].trim()
                val targetId = parts[1].trim().toInt()
                currentNode?.actions?.add(Action(label, targetId))
            }
            line.isNotBlank() -> {
                when (mode) {
                    "title" -> story.title = line.trim()
                    "data" -> story.startNodeId = org.json.JSONObject(line.trim()).getInt("start")
                    "node" -> currentNode?.text?.add(line)
                }
            }
        }
    }

    fun build(): Story = story
}

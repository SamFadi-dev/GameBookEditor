package gamebook.editor

import java.io.File
import org.json.JSONObject

fun readMiniTweeFile(path: String): Story {
    val story = Story()
    var currentNode: Node? = null
    var mode = ""

    File(path).forEachLine { line ->
        when {
            line.startsWith(":: StoryTitle") -> mode = "title"
            line.startsWith(":: StoryData") -> mode = "data"
            line.startsWith(":: ") -> {
                val id = line.removePrefix(":: ").trim().toInt()
                currentNode = Node(id)
                story.nodes[id] = currentNode
                mode = "node"
            }
            line.startsWith("[[") && line.endsWith("]]") -> {
                val parts = line.removePrefix("[[").removeSuffix("]]").split("->")
                val label = parts[0].trim()
                val targetId = parts[1].trim().toInt()
                currentNode?.actions?.add(Action(label, targetId))
            }
            line.isBlank() -> {}
            else -> {
                when (mode) {
                    "title" -> story.title = line.trim()
                    "data" -> {
                        try {
                            val json = JSONObject(line.trim())
                            story.startNodeId = json.optInt("start", -1)
                        } catch (e: Exception) {
                            println("❌ Failed to parse StoryData: ${e.message}")
                        }
                    }
                    "node" -> currentNode?.text?.add(line)
                }
            }
        }
    }

    return story
}

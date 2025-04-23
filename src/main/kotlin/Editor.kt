package gamebook.editor

import java.util.Scanner

class Editor {
    private var story: Story? = null
    private var currentNodeId: Int? = null
    private val commandManager = CommandManager()

    fun start() {
        val scanner = Scanner(System.`in`)
        while (true) {
            print(">> ")
            val input = scanner.nextLine().trim()
            val parts = input.split(" ", limit = 2)

            when (parts[0]) {
                "exit" -> {
                    println("Bye!")
                    return
                }

                "open" -> {
                    if (parts.size < 2) {
                        println("Usage: open <file path>")
                        continue
                    }
                    val path = parts[1]
                    try {
                        story = readMiniTweeFile(path)
                        currentNodeId = story?.startNodeId
                        println("Story loaded: ${story?.title}")
                        displayCurrentNode()
                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }
                }

                "node" -> {
                    if (parts.size < 2) {
                        currentNodeId = story?.startNodeId
                    } else {
                        currentNodeId = parts[1].toIntOrNull()
                    }
                    displayCurrentNode()
                }

                "set" -> {
                    val subparts = parts.getOrNull(1)?.split(" ", limit = 2)
                    if (subparts == null || subparts.size < 2) {
                        println("Usage: set text <new text>")
                        return
                    }

                    when (subparts[0]) {
                        "text" -> {
                            val newTextRaw = subparts[1]
                            val lines = newTextRaw.split("\\")
                            val node = story?.nodes?.get(currentNodeId)
                            if (node == null) {
                                println("❌ No current node selected.")
                            } else {
                                val cmd = SetTextCommand(node, lines)
                                commandManager.execute(cmd)
                                println("✅ Text updated for node ${node.id}.")
                                displayCurrentNode()
                            }
                        }
                        else -> println("Unknown set command: ${subparts[0]}")
                    }
                }

                "undo" -> {
                    commandManager.undo()
                    displayCurrentNode()
                }

                "redo" -> {
                    commandManager.redo()
                    displayCurrentNode()
                }

                "history" -> {
                    commandManager.history()
                }

                else -> println("Unknown command: ${parts[0]}")
            }
        }
    }

    private fun displayCurrentNode() {
        val story = story ?: return println("No story loaded.")
        val node = story.nodes[currentNodeId]
        if (node == null) {
            println("❌ Node $currentNodeId not found. Check that your 'start' points to an existing node.")
            return
        }


        println("\nStory Title: ${story.title}")
        println("Start Node: ${story.startNodeId}")
        println("Current Node: ${node.id}")
        println()

        node.text.forEach { println(it) }

        node.actions.forEachIndexed { index, action ->
            println("${index + 1}) ${action.label} (-> ${action.targetId})")
        }

        println()
    }
}

package gamebook.editor

import java.util.Scanner

class Editor {
    private var story: Story? = null
    private var currentNodeId: Int? = null
    private val commandManager = CommandManager()
    private val mementos = mutableListOf<Memento>()
    private var loadedFilePath: String? = null

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
                    if (!path.endsWith(".mini.twee")) {
                        println("Invalid file extension. Please open a .mini.twee file.")
                        continue
                    }
                    try {
                        story = readMiniTweeFile(path)
                        currentNodeId = story?.startNodeId
                        loadedFilePath = path
                        saveSnapshot()
                        println("Story loaded: ${story?.title}")
                        displayCurrentNode()
                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }
                }

                "node" -> {
                    currentNodeId = if (parts.size < 2) {
                        story?.startNodeId
                    } else {
                        parts[1].toIntOrNull()
                    }
                    displayCurrentNode()
                }

                "save" -> {
                    // Save in the path, and if null => save in the current file
                    val filename = parts.getOrNull(1) ?: loadedFilePath
                    if (filename == null) {
                        println("No file loaded and no filename specified.")
                        continue
                    }
                    val story = story
                    if (story == null) {
                        println("No story to save.")
                        continue
                    }

                    try {
                        val output = story.toMiniTwee()
                        java.io.File(filename).writeText(output)
                        println("Story saved to $filename")
                    } catch (e: Exception) {
                        println("Failed to save: ${e.message}")
                    }
                }

                "set" -> {
                    val subparts = parts.getOrNull(1)?.split(" ", limit = 2)
                    if (subparts == null || subparts.size < 2) {
                        println("Usage: set text <new text>")
                        continue
                    }

                    when (subparts[0]) {
                        "text" -> {
                            val newTextRaw = subparts[1]
                            val lines = newTextRaw.split("\\")
                            if (story == null || currentNodeId == null) {
                                println("No story or current node selected.")
                                continue
                            }

                            val cmd = CreateAndSetTextCommand(story!!, currentNodeId!!, lines)
                            commandManager.execute(cmd)
                            saveSnapshot()
                            println("✅ Text updated for node $currentNodeId.")
                            displayCurrentNode()
                        }

                        "action" -> {
                            val args = subparts.getOrNull(1)?.split(" ", limit = 3)
                            if (args == null || args.size < 3) {
                                println("Usage: set action <index> <targetId> <label>")
                                continue
                            }

                            val index = args[0].toIntOrNull()
                            val targetId = args[1].toIntOrNull()
                            val label = args[2]

                            if (index == null || targetId == null || targetId < 0 || index < 0) {
                                println("Invalid index or targetId.")
                                continue
                            }

                            val node = story?.nodes?.get(currentNodeId)
                            if (node == null) {
                                println("/!\\ No current node selected.")
                                continue
                            }

                            val cmd = SetActionCommand(node, index, Action(label, targetId))
                            commandManager.execute(cmd)
                            println("✅ Action set.")
                            displayCurrentNode()
                            saveSnapshot()
                        }

                        "ids" -> {
                            val args = subparts.getOrNull(1)?.split(" ")
                            if (args == null || args.size != 3) {
                                println("Usage: set ids <id from> <id to> <+/-diff>")
                                continue
                            }

                            val from = args[0].toIntOrNull()
                            val to = args[1].toIntOrNull()
                            val diff = args[2].toIntOrNull()

                            if (from == null || to == null || diff == null) {
                                println("Invalid arguments.")
                                continue
                            }

                            try {
                                val cmd = SetIdsCommand(story!!, from, to, diff)
                                commandManager.execute(cmd)
                                if (currentNodeId in from..to) {
                                    currentNodeId = currentNodeId?.plus(diff)
                                }
                                saveSnapshot()
                                println("✅ Node IDs updated.")
                                displayCurrentNode()
                            } catch (e: Exception) {
                                println(e.message)
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

                // Debugging purpose
                "list" -> {
                    story?.nodes?.keys?.sorted()?.forEach { id ->
                        println("Node ID: $id")
                    }
                }

                "revert" -> {
                    val index = parts.getOrNull(1)?.toIntOrNull()
                    if (index == null || index < 0 || index >= mementos.size) {
                        println("Usage: revert <index> (0 to ${mementos.size-1})")
                        continue
                    }

                    val snapshot = mementos[mementos.size - 1 - index]
                    story = snapshot.storySnapshot.storyCopy()
                    currentNodeId = snapshot.currentNodeId
                    println("Reverted to state #$index")
                    displayCurrentNode()
                }

                else -> println("Unknown command: ${parts[0]}")
            }
        }
    }

    private fun displayCurrentNode() {
        val story = story ?: return println("No story loaded.")
        val node = story.nodes[currentNodeId]
        if (node == null) {
            println("\nNode $currentNodeId does not exist yet. You can create it with 'set text ...'")
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

    private fun saveSnapshot() {
        story?.let {
            mementos.add(Memento(it.storyCopy(), currentNodeId))
        }
    }

}

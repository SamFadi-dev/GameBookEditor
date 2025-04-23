package gamebook.editor

class CommandManager {
    private val undoStack = mutableListOf<Command>()
    private val redoStack = mutableListOf<Command>()

    fun execute(command: Command) {
        command.execute()
        undoStack.add(command)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val command = undoStack.removeLast()
            command.undo()
            redoStack.add(command)
        } else {
            println("Nothing to undo.")
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val command = redoStack.removeLast()
            command.execute()
            undoStack.add(command)
        } else {
            println("Nothing to redo.")
        }
    }

    fun history() {
        println("History:")
        undoStack.forEachIndexed { i, cmd ->
            println("${i}. ${cmd.description}")
        }
    }
}

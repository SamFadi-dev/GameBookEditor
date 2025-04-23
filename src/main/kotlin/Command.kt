package gamebook.editor

interface Command {
    fun execute()
    fun undo()
    val description: String
}
package gamebook.editor

data class Node(
    val id: Int,
    val text: MutableList<String> = mutableListOf(),
    val actions: MutableList<Action> = mutableListOf()
)

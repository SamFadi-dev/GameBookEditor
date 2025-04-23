package gamebook.editor

data class Story(
    var title: String = "",
    var startNodeId: Int = -1,
    val nodes: MutableMap<Int, Node> = mutableMapOf()
)

package gamebook.editor

data class Memento(
    val storySnapshot: Story,
    val currentNodeId: Int?
)

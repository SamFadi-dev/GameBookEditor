package gamebook.editor

import java.io.File

fun readMiniTweeFile(path: String): Story {
    val builder = StoryBuilder()
    File(path).forEachLine { builder.processLine(it) }
    return builder.build()
}
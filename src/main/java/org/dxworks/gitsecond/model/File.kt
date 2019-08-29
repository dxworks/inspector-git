package org.dxworks.gitsecond.model

import java.nio.file.Path
import java.nio.file.Paths

data class File(var fullyQualifiedName: String, var changes: MutableList<Change>) {

    var name = fullyQualifiedName.split("/").last()

    val path: Path
        get() = Paths.get(fullyQualifiedName)
}

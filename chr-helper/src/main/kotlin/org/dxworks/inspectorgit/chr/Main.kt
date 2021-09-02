package org.dxworks.inspectorgit.chr

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.dxworks.inspectorgit.gitclient.iglog.readers.IGLogReader
import org.dxworks.inspectorgit.model.git.Change
import org.dxworks.inspectorgit.model.git.ChangeType
import org.dxworks.inspectorgit.transformers.GitProjectTransformer
import org.dxworks.inspectorgit.utils.devNull
import java.io.FileFilter
import java.nio.file.Paths

fun main(args: Array<String>) {
    val folder = Paths.get(args.firstOrNull() ?: ".").toFile()
    folder.isDirectory

    val files = folder.listFiles(FileFilter { it.extension == "iglog" }) ?: return
    val doPrefix = files.size > 1

    val changeMeta = files.map {
        GitProjectTransformer(
            IGLogReader().read(it.inputStream()),
            it.nameWithoutExtension
        ).transform()
    }.flatMap { project ->
        project.commitRegistry.all.filterNot { it.isMergeCommit }.flatMap { it.changes }.map { it to project.name }
    }.associate {
        val change = it.first
        getId(it, doPrefix) to mapOf(
            "fileSize" to change.annotatedLines.size,
            "isAlive" to !(it == change.file.changes.last() && change.type == ChangeType.DELETE)
        )
    }

    val file = folder.resolve("chr-help.json")
    file.createNewFile()
    jacksonObjectMapper().writeValue(file, changeMeta)
}

private fun getId(
    changeAndProjectName: Pair<Change, String>,
    doPrefix: Boolean
) = (getFileName(changeAndProjectName.first) + changeAndProjectName.first.commit.id).let {
    if (doPrefix)
        "${changeAndProjectName.second}/$it${changeAndProjectName.second}"
    else
        it
}

fun getFileName(change: Change) = if (change.oldFileName == devNull) change.newFileName else change.oldFileName

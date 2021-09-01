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
    val folder = Paths.get(args[0]).toFile()
    folder.isDirectory

    val changeMeta = folder.listFiles(FileFilter { it.extension == "iglog" })?.map {
        GitProjectTransformer(
            IGLogReader().read(it.inputStream()),
            it.nameWithoutExtension
        ).transform()
    }?.flatMap {
        it.commitRegistry.all.filterNot { it.isMergeCommit }.flatMap { it.changes }
    }?.associate {
        getFileName(it) + it.commit.id to mapOf(
            "fileSize" to it.annotatedLines.size,
            "isAlive" to !(it == it.file.changes.last() && it.type == ChangeType.DELETE)
        )
    }

    val file = folder.resolve("chr-help.json")
    file.createNewFile()
    jacksonObjectMapper().writeValue(file, changeMeta)
}

fun getFileName(it: Change) = if (it.oldFileName == devNull) it.newFileName else it.oldFileName

package org.dxworks.inspectorgit.gitclient.extractors.impl

import com.github.difflib.DiffUtils
import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.LineOperation
import org.junit.jupiter.api.Test

internal class HunkChangeMetaExtractorTest {

    private val hunkChangeMetaExtractor = HunkChangeMetaExtractor()

    @Test
    fun extract() {
        val hunkDTO = HunkDTO(listOf(
                LineChangeDTO(LineOperation.ADD, 1, "        if (changes.size < commit.parents.size && !changes.all { it.type == ChangeType.DELETE }) {"),
                LineChangeDTO(LineOperation.ADD, 2, "            val cleanParent = commit.parents.first { changes.none { change -> change.parentCommits.first() == it } }"),
                LineChangeDTO(LineOperation.ADD, 3, "            val lastChange = firstChange.file.getLastChange(cleanParent)!!"),
                LineChangeDTO(LineOperation.ADD, 4, "            firstChange.annotatedLines = lastChange.annotatedLines.map { AnnotatedLine(it.number, it.content) }"),
                LineChangeDTO(LineOperation.ADD, 5, "        } else {"),

                LineChangeDTO(LineOperation.DELETE, 1, "        if (changes.size < commit.parents.size && !changes.all { it.type == ChangeType.DELETE }) {"),
                LineChangeDTO(LineOperation.DELETE, 2, "            val cleanParent = commit.parents.first { changes.none { change -> change.parentCommit == it } }"),
                LineChangeDTO(LineOperation.DELETE, 3, "            firstChange.file.getLastChange(cleanParent)?.let { lastChange ->"),
                LineChangeDTO(LineOperation.DELETE, 4, "                if (lastChange.annotatedLines.size == firstChange.annotatedLines.size)"),
                LineChangeDTO(LineOperation.DELETE, 5, "                firstChange.annotatedLines = lastChange.annotatedLines.map { AnnotatedLine(it.number, it.content) }"),
                LineChangeDTO(LineOperation.DELETE, 6, "            }"),
                LineChangeDTO(LineOperation.DELETE, 6, "        } else {")
        ))

        val line = hunkChangeMetaExtractor.write(hunkDTO)
        val hunkChangeMeta = hunkChangeMetaExtractor.read(line)
        println(hunkChangeMeta)
    }

    @Test
    fun diffTestCode() {
        val old = "        if (changes.size < commit.parents.size && !changes.all { it.type == ChangeType.DELETE }) {\n" +
                "            val cleanParent = commit.parents.first { changes.none { change -> change.parentCommits.first() == it } }\n" +
                "            val lastChange = firstChange.file.getLastChange(cleanParent)!!\n" +
                "            firstChange.annotatedLines = lastChange.annotatedLines.map { AnnotatedLine(it.number, it.content) }\n" +
                "        } else {"

        val new = "        if (changes.size < commit.parents.size && !changes.all { it.type == ChangeType.DELETE }) {\n" +
                "            val cleanParent = commit.parents.first { changes.none { change -> change.parentCommit == it } }\n" +
                "            firstChange.file.getLastChange(cleanParent)?.let { lastChange ->\n" +
                "                if (lastChange.annotatedLines.size == firstChange.annotatedLines.size)\n" +
                "                firstChange.annotatedLines = lastChange.annotatedLines.map { AnnotatedLine(it.number, it.content) }\n" +
                "            }\n" +
                "        } else {"

        val diff = DiffUtils.diff(old.toList(), new.toList())
        println(diff)
    }
}
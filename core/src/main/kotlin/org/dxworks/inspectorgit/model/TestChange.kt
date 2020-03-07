package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.dto.AnnotatedLineDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.slf4j.LoggerFactory

class TestChange(
        commit: Commit,
        type: ChangeType,
        oldFileName: String,
        newFileName: String,
        file: File,
        parentCommit: Commit?,
        lineChanges: List<LineChange>,
        parentChange: Change?,
        gitClient: GitClient
) : Change(
        commit = commit,
        type = type,
        oldFileName = oldFileName,
        newFileName = newFileName,
        file = file,
        parentCommit = parentCommit,
        lineChanges = lineChanges,
        parentChange = parentChange
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(TestChange::class.java)
    }

    init {
        if (!file.isBinary && type != ChangeType.DELETE) {
            val blame = gitClient.blame(commit.id, newFileName)
            if (blame != null) {
                if (!blameAndFileContentAreTheSame(blame))
                    LOG.error("File $newFileName is not correctly built in ${commit.id} from ${parentCommit?.id}.file last changed in ${this.parentChange?.commit?.id}")
            } else
                LOG.warn("Blame is null for $newFileName in ${commit.id}")
        }
    }


    private fun blameAndFileContentAreTheSame(blame: List<String>): Boolean {
        if (blame.size != annotatedLines.size) {
            LOG.error("$newFileName blames have a different number of lines: blame: ${blame.size}, IG: ${annotatedLines.size}")
            return false
        }
        val annotatedLineDTOs = blame.map { parseAnnotatedLine(it) }
        var ok = true
        for (i in 1 until annotatedLineDTOs.size) {
            val annotatedLineDTO = annotatedLineDTOs[i]
            val annotatedLine = annotatedLines[i]
            if (!linesAreTheSame(annotatedLineDTO, annotatedLine)) {
//                LOG.error("$newFileName is not correct in $commitId because:\n$annotatedLineDTO differs from $annotatedLine")
                ok = false
            }
        }
        return ok
    }

    private fun linesAreTheSame(annotatedLineDTO: AnnotatedLineDTO, annotatedLine: AnnotatedLine): Boolean {
        val numberAndContentAreTheSame = annotatedLineDTO.number == annotatedLine.number &&
                annotatedLineDTO.content == annotatedLine.content.content
        val lineDTOCommitId = annotatedLineDTO.commitId
        val lineCommitId = annotatedLine.content.commit.id
        val commitsAreEqual = if (lineDTOCommitId.startsWith("^"))
            lineCommitId.startsWith(lineDTOCommitId.removePrefix("^"))
        else
            lineCommitId == lineDTOCommitId
        if (!commitsAreEqual)
            LOG.warn("In $newFileName at ${commit.id} at line ${annotatedLineDTO.number} commits differ blame: $lineDTOCommitId, IG: $lineCommitId")
        return numberAndContentAreTheSame
    }

    private fun parseAnnotatedLine(it: String): AnnotatedLineDTO {
        val commitDelimiterIndex = it.indexOf(" ")
        val commitId = it.substring(0, commitDelimiterIndex)
        val other = it.substring(commitDelimiterIndex + 1)
        val contentDelimiterIndex = getContentDelimiterIndex(other)
        val authorTimeLineNo = other.substring(0, contentDelimiterIndex)
        val content = other.substring(contentDelimiterIndex + 2)
        val lineNumber = authorTimeLineNo.substring(authorTimeLineNo.lastIndexOf(" ") + 1).toInt()
        return AnnotatedLineDTO(commitId, lineNumber, content)
    }

    private fun getContentDelimiterIndex(other: String): Int {
        var counter = 1
        val startIndex = other.indexOf("(") + 1
        val tail = other.substring(startIndex)
        for (i in tail.indices) {
            if (tail[i] == '(')
                counter++
            if (tail[i] == ')')
                counter--
            if (counter == 0)
                return startIndex + i
        }
        return other.indexOf(")")
    }
}
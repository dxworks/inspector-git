import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.dto.AnnotatedLineDTO
import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.model.*
import org.slf4j.LoggerFactory

class TestChange(
        commit: Commit,
        type: ChangeType,
        file: File,
        parentCommits: List<Commit>,
        oldFileName: String,
        newFileName: String,
        lineChanges: List<LineChange>,
        parentCommit: Commit?,
        gitClient: GitClient
) : Change(
        commit = commit,
        type = type,
        file = file,
        parentCommits = parentCommits,
        oldFileName = oldFileName,
        newFileName = newFileName,
        lineChanges = lineChanges,
        parentCommit = parentCommit
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(TestChange::class.java)
    }

    init {
        val blame = gitClient.blame(commit.id, newFileName)
        if (blame != null) {
            if (!blameAndFileContentAreTheSame(blame, commit.id))
                LOG.error("File $newFileName is not correctly built")
        } else
            LOG.warn("Blame is null for $newFileName in ${commit.id}")
    }


    private fun blameAndFileContentAreTheSame(blame: List<String>, commitId: String): Boolean {
        if (blame.size != annotatedLines.size) {
            LOG.error("$newFileName blames have a different number of lines")
            return false
        }
        val annotatedLineDTOs = blame.map { parseAnnotatedLine(it) }
        for (i in 1 until annotatedLineDTOs.size) {
            val annotatedLineDTO = annotatedLineDTOs[i]
            val annotatedLine = annotatedLines[i]
            if (!linesAreTheSame(annotatedLineDTO, annotatedLine, newFileName, commitId)) {
                LOG.error("$newFileName is not correct in $commitId because:\n$annotatedLineDTO differs from $annotatedLine")
                return false
            }
        }
        return true
    }

    private fun linesAreTheSame(annotatedLineDTO: AnnotatedLineDTO, annotatedLine: AnnotatedLine, fileName: String, commitId: String): Boolean {
        val numberAndContentAreTheSame = annotatedLineDTO.number == annotatedLine.number &&
                annotatedLineDTO.content == annotatedLine.content
        val lineDTOCommitId = annotatedLineDTO.commitId
        val lineCommitId = annotatedLine.commit.id
        val commitsAreEqual = if (lineDTOCommitId.startsWith("^"))
            lineCommitId.startsWith(lineDTOCommitId.removePrefix("^"))
        else
            lineCommitId == lineDTOCommitId
        if (!commitsAreEqual)
            LOG.warn("In $fileName at $commitId at line ${annotatedLineDTO.number} commits differ blame: $lineDTOCommitId, IG: $lineCommitId")
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
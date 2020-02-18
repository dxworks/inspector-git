package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.gitClient.dto.AnnotatedLineDTO
import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.AuthorId
import org.dxworks.inspectorgit.model.Commit

data class WorkAnalyzerResult(
        val commit: Commit,
        var newWork: MutableList<AnnotatedLine> = ArrayList(),
        var legacyRefactor: MutableList<CodeChange> = ArrayList(),
        var helpOthers: MutableList<CodeChange> = ArrayList(),
        var churn: MutableList<AnnotatedLine> = ArrayList()
)

class WorkAnalyzerNumbersDTO {
    lateinit var commitId: String
    lateinit var authorId: AuthorId
    var newWork: Int = 0
    var legacyRefactor: Int = 0
    var helpOthers: Int = 0
    var churn: Int = 0

    companion object {
        fun get(result: WorkAnalyzerResult): WorkAnalyzerNumbersDTO {
            val dto = WorkAnalyzerNumbersDTO()
            dto.commitId = result.commit.id
            dto.authorId = result.commit.author.id
            dto.newWork = result.newWork.size
            dto.churn = result.churn.size
            dto.legacyRefactor = result.legacyRefactor.size
            dto.helpOthers = result.helpOthers.size
            return dto
        }
    }
}

class WorkAnalyzerResultDTO {
    lateinit var commitId: String
    lateinit var authorId: AuthorId
    var newWork: List<AnnotatedLineDTO> = emptyList()
    var legacyRefactor: List<CodeChangeDTO> = emptyList()
    var helpOthers: List<CodeChangeDTO> = emptyList()
    var churn: List<AnnotatedLineDTO> = emptyList()

    companion object {
        fun get(result: WorkAnalyzerResult): WorkAnalyzerResultDTO {
            val dto = WorkAnalyzerResultDTO()
            dto.commitId = result.commit.id
            dto.authorId = result.commit.author.id
            dto.newWork = result.newWork.map { AnnotatedLineDTO(it.content.commit.id, it.number) }
            dto.churn = result.churn.map { AnnotatedLineDTO(it.content.commit.id, it.number) }
            dto.legacyRefactor = result.legacyRefactor.map { CodeChangeDTO.get(it) }
            dto.helpOthers = result.helpOthers.map { CodeChangeDTO.get(it) }
            return dto
        }
    }
}

class CodeChangeDTO {
    var addedLine: AnnotatedLineDTO? = null
    var removedLine: AnnotatedLineDTO? = null

    companion object {
        fun get(codeChange: CodeChange): CodeChangeDTO {
            val dto = CodeChangeDTO()
            val addedLine = codeChange.addedLine
            dto.addedLine = AnnotatedLineDTO(addedLine.content.commit.id, addedLine.number)
            val removedLine = codeChange.removedLine
            dto.removedLine = AnnotatedLineDTO(removedLine.content.commit.id, removedLine.number)
            return dto
        }
    }
}

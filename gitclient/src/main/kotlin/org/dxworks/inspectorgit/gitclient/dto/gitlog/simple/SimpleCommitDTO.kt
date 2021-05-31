package org.dxworks.inspectorgit.gitclient.dto.gitlog.simple

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.utils.commitDateTimeFormatter
import org.dxworks.inspectorgit.utils.devNull
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class SimpleCommitDTO(
    private val commitDTO: CommitDTO
) {

    private val commit = commitDTO.id
    private val author = commitDTO.authorName
    private val email = commitDTO.authorEmail
    private val date = commitDTO.authorDate.let { ZonedDateTime.parse(it, commitDateTimeFormatter) }
    private val message = commitDTO.message

    override fun toString(): String {
        return """
            commit:$commit
            author:$author
            email:$email
            date:${date.format(simpleCommitDateTimeFormatter)}
            message:
            $message
            
            numstat:
            
        """.trimIndent() + getNumstat()
    }

    private fun getNumstat(): String {
        return commitDTO.changes.joinToString(separator = "\n", prefix = "\n") {
            ":${idPairs(it)} ${idPairs(it)} ${type(it)}\t ${names(it)}"
        } + commitDTO.changes.joinToString(separator = "\n", prefix = "\n") {
            "${added(it)}\t${deleted(it)} ${name(it)}"
        }
    }

    private fun name(it: ChangeDTO) = if (it.newFileName == devNull) it.oldFileName else it.newFileName

    private fun added(it: ChangeDTO) = it.hunks.flatMap { it.addedLineChanges }.count()

    private fun deleted(it: ChangeDTO) = it.hunks.flatMap { it.deletedLineChanges }.count()

    private fun names(it: ChangeDTO) = when {
        it.oldFileName == devNull -> it.newFileName
        it.newFileName == devNull -> it.oldFileName
        else -> "${it.oldFileName} ${it.newFileName}"
    }

    private fun type(it: ChangeDTO) = when (it.type) {
        ChangeType.ADD -> "A"
        ChangeType.DELETE -> "D"
        ChangeType.RENAME -> "R"
        ChangeType.MODIFY -> "M"
    }

    private fun idPairs(it: ChangeDTO) =
        "${if (it.type == ChangeType.ADD) id0 else id} ${if (it.type == ChangeType.DELETE) id0 else id}"

    private val id: String
        get() = Random(System.currentTimeMillis()).nextLong(1000000L, 10000000L).toString()

    private val id0: String = "0000000"
}

private const val commitDateFormat = "EEE, d MMM yyyy HH:mm:ss Z"
private val simpleCommitDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(commitDateFormat)

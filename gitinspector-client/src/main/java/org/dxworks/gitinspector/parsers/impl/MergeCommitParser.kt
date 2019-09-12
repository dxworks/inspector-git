package org.dxworks.gitinspector.parsers.impl

import org.dxworks.gitinspector.GitClient
import org.dxworks.gitinspector.dto.ChangeDTO
import org.dxworks.gitinspector.parsers.abstracts.CommitParser

class MergeCommitParser(private val gitClient: GitClient) : CommitParser() {

    override fun extractChanges(lines: MutableList<String>, commitId: String, parentIds: List<String>): List<ChangeDTO> {
        val parentAndFile: List<Pair<String, String>> = getAffectedFilesByParent(commitId, parentIds)
        return parentAndFile.map { Pair(it.first, gitClient.diff(it.first, commitId, it.second)) }
                .filter { it.second.isNotEmpty() }
                .map { BlameParser(gitClient, commitId, it.first).parse(it.second.toMutableList()) }
    }

    private fun getAffectedFilesByParent(commitId: String, parentIds: List<String>): List<Pair<String, String>> {
        val affectedFiles = gitClient.affectedFiles(commitId)
        val parentAndFile: MutableList<Pair<String, String>> = ArrayList()
        var index = 0
        for (fileName in affectedFiles) {
            if (fileName.isNotBlank())
                parentAndFile.add(Pair(parentIds[index], fileName))
            else
                index++
        }
        return parentAndFile
    }
}
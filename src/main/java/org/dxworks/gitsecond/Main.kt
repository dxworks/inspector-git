package org.dxworks.gitsecond

import org.dxworks.gitsecond.transformers.createProject
import org.eclipse.jgit.api.errors.GitAPIException

const val REPO_NAME = "gitTest"

fun main(args: Array<String>) {
    val gitClient = GitClient()

    try {
        gitClient.cloneAndInitializeRepository("https://github.com/nagyDarius/gitLogTest.git", REPO_NAME)

    } catch (e: GitAPIException) {
        e.printStackTrace()
    }

    val commitDatas = gitClient.generateGitLogForDx(REPO_NAME).reversed()
    println(commitDatas)

    val project = createProject(commitDatas, REPO_NAME)
    println(project)

//    val blames = project.commitRegistry.all
//            .filter { it.isMergeCommit }
//            .flatMap { it.changes }
//            .mapNotNull { change -> change.file?.let { gitClient.blame(REPO_NAME, it.fullyQualifiedName, change.commit.id, project) } }

}
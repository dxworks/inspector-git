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
    commitDatas.map { "${it.message} ${it.isMergeCommit}" }.forEach { println(it) }

    val project = createProject(commitDatas, REPO_NAME)
    println(project)

//Show the entire history of a file (including history beyond renames):
//$ git log --follow -p -- <file>
}
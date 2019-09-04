package org.dxworks.gitsecond

import org.dxworks.gitinspector.GitClient
import org.dxworks.gitinspector.parsers.LogParser
import org.dxworks.gitsecond.transformers.createProject
import java.nio.file.Paths

fun main() {
    val gitClient = GitClient(Paths.get(System.getProperty("user.home"), "Documents/dx/test"))
    val projectDTO = LogParser(gitClient).parse(gitClient.getLogs().toMutableList())
    val project = createProject(projectDTO, "TestProject")
    println(projectDTO)
    println(project)
}
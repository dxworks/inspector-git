package org.dxworks.gitsecond

import org.dxworks.gitinspector.GitClient
import org.dxworks.gitinspector.parsers.LogParser
import org.dxworks.gitsecond.transformers.OptimusSecond
import java.nio.file.Paths

fun main() {
    val gitClient = GitClient(Paths.get("/home/darius/.dx-platform/projects/kafka/repository/kafka"))
    val projectDTO = LogParser(gitClient).parse(gitClient.getLogs().toMutableList())
    val project = OptimusSecond.createProject(projectDTO, "TestProject")
    println(projectDTO)
    println(project)
}
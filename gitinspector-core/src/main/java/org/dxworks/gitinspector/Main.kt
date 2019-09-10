package org.dxworks.gitinspector

import org.dxworks.gitinspector.dto.ProjectDTO
import org.dxworks.gitinspector.transformers.ProjectTransformer
import org.dxworks.gitinspector.utils.DTO_FOLDER_PATH
import org.dxworks.gitinspector.utils.Helper

fun main() {
    val project = ProjectTransformer.createProject(Helper.jsonFromFile(DTO_FOLDER_PATH.resolve("kafka.json"), ProjectDTO::class.java), "kafka")
    println(project)
//    Helper.deleteFile(DTO_FOLDER_PATH.resolve("kafka.json"))
}
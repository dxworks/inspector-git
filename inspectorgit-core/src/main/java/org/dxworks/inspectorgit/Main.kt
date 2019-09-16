package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.utils.DTO_FOLDER_PATH
import org.dxworks.inspectorgit.utils.Helper

fun main() {
    val project = ProjectTransformer.createProject(Helper.jsonFromFile(DTO_FOLDER_PATH.resolve("kafka.json"), ProjectDTO::class.java), "kafka")
    println(project)
//    Helper.deleteFile(DTO_FOLDER_PATH.resolve("kafka.json"))
}
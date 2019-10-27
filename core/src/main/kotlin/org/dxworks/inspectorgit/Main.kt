package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.JsonUtils

fun main() {
    val project = ProjectTransformer.createProject(JsonUtils.jsonFromFile(FileSystemUtils.getDtoFileFor("kafka", "trunk"), ProjectDTO::class.java), "kafka")
    println(project)
}
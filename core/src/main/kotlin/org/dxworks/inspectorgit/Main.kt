package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.JsonUtils

fun main() {

    val projectDTO = JsonUtils.jsonFromFile(FileSystemUtils.getDtoFilePathFor("kafka", "trunk"), ProjectDTO::class.java)
    val project = ProjectTransformer(projectDTO, "kafka").transform()
    println(project.hashCode())
}
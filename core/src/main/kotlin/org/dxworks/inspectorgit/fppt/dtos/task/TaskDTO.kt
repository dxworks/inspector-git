package org.dxworks.inspectorgit.fppt.dtos.task

class TaskDTO(
        val id: String,
        val parentId: String?,
        val type: String,
        val status: String,
        val summary: String,
        val description: String,
        val created: String, // example 2020-04-07T08:30:48.589+0300
        val updated: String,
        val subtaskIds: List<String>

)
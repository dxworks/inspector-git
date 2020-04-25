package org.dxworks.inspectorgit.fppt.configuration

import java.nio.file.Path

class FpptConfiguration(
        val repositoryPath: Path,
        val taskPrefixes: List<String> = emptyList(),
        val devMergesFilePath: Path? = null,
        val tasksFilePath: Path? = null
)
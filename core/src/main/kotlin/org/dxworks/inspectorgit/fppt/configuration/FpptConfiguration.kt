package org.dxworks.inspectorgit.fppt.configuration

import java.nio.file.Path

class FpptConfiguration(
        val repositoryPath: Path?,
        val iglogPath: Path?,
        val taskPrefixes: List<String> = emptyList(),
        val devMergesFilePath: Path?,
        val tasksFilePath: Path?,
        val remoteInfoPath: Path?
)
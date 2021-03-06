package org.dxworks.inspectorgit.utils

import java.nio.file.Path
import java.nio.file.Paths
import java.time.format.DateTimeFormatter


const val dateFormat = "EEE MMM d HH:mm:ss yyyy Z"
val commitDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat)


const val devNull = "dev/null"
const val appFolderName = ".inspectorgit"
val userHomePath: Path = Paths.get(System.getProperty("user.home"))
val appFolderPath: Path = userHomePath.resolve(appFolderName)
val propertyFilePath: Path = appFolderPath.resolve(Paths.get("inspector.properties"))

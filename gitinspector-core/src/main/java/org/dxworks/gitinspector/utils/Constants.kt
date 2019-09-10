package org.dxworks.gitinspector.utils

import java.nio.file.Path
import java.nio.file.Paths

private const val APP_FOLDER_NAME = ".gitinspector"
private const val DTO_FOLDER_NAME = "jsonProjects"
val APP_FOLDER_PATH: Path = Paths.get(System.getProperty("user.home"), APP_FOLDER_NAME)
val DTO_FOLDER_PATH: Path = APP_FOLDER_PATH.resolve(DTO_FOLDER_NAME)

val devNull = "dev/null"
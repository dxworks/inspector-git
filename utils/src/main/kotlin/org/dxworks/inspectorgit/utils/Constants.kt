package org.dxworks.inspectorgit.utils

import java.nio.file.Path
import java.nio.file.Paths


const val devNull = "dev/null"
const val appFolderName = ".inspectorgit"
val userHomePath: Path = Paths.get(System.getProperty("user.home"))
val appFolderPath: Path = userHomePath.resolve(appFolderName)
val propertyFilePath: Path = appFolderPath.resolve(Paths.get("inspector.properties"))

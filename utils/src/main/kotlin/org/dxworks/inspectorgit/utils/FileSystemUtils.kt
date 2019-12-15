package org.dxworks.inspectorgit.utils

import java.nio.file.Path

class FileSystemUtils {
    companion object {
        private const val PROJECTS_FOLDER_NAME = "projects"
        private val PROJECTS_FOLDER_PATH: Path = appFolderPath.resolve(PROJECTS_FOLDER_NAME)

        fun getRepoFolderPath(path: String): Path {
            return PROJECTS_FOLDER_PATH.resolve(path)
        }

        fun deleteRepository(path: String): Boolean {
            return PROJECTS_FOLDER_PATH.resolve(path).toFile().deleteRecursively()
        }
    }
}
package org.dxworks.inspectorgit.utils

import java.nio.file.Path
import java.nio.file.Paths

class FileSystemUtils {
    companion object {
        private const val APP_FOLDER_NAME = ".inspectorgit"
        private const val PROJECTS_FOLDER_NAME = "projects"
        private const val PROJECT_DTO_FOLDER_NAME = "DTO"
        private val APP_FOLDER_PATH: Path = Paths.get(System.getProperty("user.home"), APP_FOLDER_NAME)
        private val PROJECTS_FOLDER_PATH: Path = APP_FOLDER_PATH.resolve(PROJECTS_FOLDER_NAME)

        fun getDtoFolderPathFor(repoName: String): Path {
            return PROJECTS_FOLDER_PATH.resolve(repoName).resolve(PROJECT_DTO_FOLDER_NAME)
        }

        fun getDtoFileFor(repoName: String, branch: String): Path {
            return getDtoFolderPathFor(repoName).resolve("$branch.json")
        }

        fun deleteFile(path: Path) = path.toFile().delete()
    }
}
package org.dxworks.inspectorgit.utils

import java.io.File
import java.nio.file.Path

class FileSystemUtils {
    companion object {
        private const val PROJECTS_FOLDER_NAME = "projects"
        private const val PROJECT_DTO_FOLDER_NAME = "DTO"
        private val PROJECTS_FOLDER_PATH: Path = appFolderPath.resolve(PROJECTS_FOLDER_NAME)

        fun getDtoFolderPathFor(repoName: String): Path {
            return PROJECTS_FOLDER_PATH.resolve(repoName).resolve(PROJECT_DTO_FOLDER_NAME)
        }

        fun getDtoFilePathFor(repoName: String, branch: String): Path {
            return getDtoFolderPathFor(repoName).resolve("$branch.json")
        }

        fun deleteFile(file: File) = file.delete()
    }
}
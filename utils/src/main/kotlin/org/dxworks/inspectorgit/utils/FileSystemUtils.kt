package org.dxworks.inspectorgit.utils

import com.google.gson.Gson
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

        fun writeResults(systemId: String, results: Map<String, Collection<Any>>?) {
            val file = userHomePath.resolve("Documents").resolve("$systemId.json").toFile()
            file.writeText(Gson().toJson(results))
        }
    }
}
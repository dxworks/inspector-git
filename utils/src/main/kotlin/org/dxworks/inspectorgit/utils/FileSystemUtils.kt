package org.dxworks.inspectorgit.utils

import com.google.gson.Gson
import java.awt.Desktop
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

        fun getScriptResultsPathForSystem(id: String) = appFolderPath.resolve("script-output").resolve(id)

        fun getScriptResult(id: String, fileName: String): String {
            return getScriptResultsPathForSystem(id).resolve(fileName).toFile().readText()
        }

        fun openFile(id: String, fileName: String) {
            val file = getScriptResultsPathForSystem(id).resolve(fileName).toFile()
            if (OsUtils.isWindows) {
                Runtime.getRuntime().exec(arrayOf("rundll32", "url.dll,FileProtocolHandler",
                        file.absolutePath))
            } else if (OsUtils.isUnix) {
                Runtime.getRuntime().exec(arrayOf("/usr/bin/open",
                        file.absolutePath))
            } else {
                // Unknown OS, try with desktop
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file)
                }
            }
        }

        fun getSystemFolder(id: String) = systemsFolderPath.resolve(id).toFile()
    }
}
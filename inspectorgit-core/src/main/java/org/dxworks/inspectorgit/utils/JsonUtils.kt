package org.dxworks.inspectorgit.utils

import com.google.gson.Gson
import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.dto.ProjectDTO
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Slf4j
class JsonUtils {
    companion object {
        private val LOG = LoggerFactory.getLogger(JsonUtils::class.java)

        fun toJsonFile(path: Path, entity: Any) {
            Files.createDirectories(path.parent)
            path.toFile().writeText(Gson().toJson(entity))
        }

        fun <T> jsonFromFile(path: Path, classOfT: Class<T>): T {
            return Gson().fromJson(path.toFile().readText(), classOfT)
        }

        fun deleteFile(path: Path): Boolean {
            val deleted = path.toFile().delete()
            return if (deleted) {
                LOG.info("Deleted file: $path")
                true
            } else {
                LOG.error("Could not delete file: $path")
                false
            }
        }
    }
}
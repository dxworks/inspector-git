package org.dxworks.inspectorgit.utils

import com.google.gson.Gson
import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.dto.ProjectDTO
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Slf4j
class Helper {
    companion object {
        private val LOG = LoggerFactory.getLogger(Helper::class.java)

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

fun main() {
    Helper.toJsonFile(Paths.get("/home/darius/Documents/dx/kafka/1caaf6db400df7e37b7f0416bb83ab451018a5c8.json"), Helper.jsonFromFile(DTO_FOLDER_PATH.resolve("kafka.json"), ProjectDTO::class.java).commits.filter{it.id == "3f432cd01c4728396f277e33897f6f898c530c99"})
}
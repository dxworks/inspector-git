package org.dxworks.inspectorgit.utils

import com.google.gson.Gson
import java.nio.file.Files
import java.nio.file.Path

class JsonUtils {
    companion object {
        fun toJsonFile(path: Path, entity: Any) {
            Files.createDirectories(path.parent)
            path.toFile().writeText(toJson(entity))
        }

        fun <T> jsonFromFile(path: Path, classOfT: Class<T>): T = fromJson(path.toFile().readText(), classOfT)

        fun toJson(entity: Any): String = Gson().toJson(entity)

        fun <T> fromJson(json: String, classOfT: Class<T>): T = Gson().fromJson(json, classOfT)
    }
}
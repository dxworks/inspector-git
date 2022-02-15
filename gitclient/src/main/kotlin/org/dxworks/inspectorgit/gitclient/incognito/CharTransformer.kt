package org.dxworks.inspectorgit.gitclient.incognito

import java.io.File

const val CHAR_MAP_ENV_VARIABLE = "INCOGNITO_CHARMAP_FILE"
val charmapFilePath: String? = System.getenv(CHAR_MAP_ENV_VARIABLE)
val charTransformer: CharTransformer =
    if (charmapFilePath != null && File(charmapFilePath).exists())
        CharTransformer(charmapFilePath)
    else CharTransformer()

fun encryptString(name: String): String = name.map { charTransformer.mapChar(it) }.joinToString("")


class CharTransformer {
    constructor() {
        checkDefaultFileExistsAndCreateIfNecessary()
        charMap = readCharMapFromFile(DEFAULT_FILE)
    }

    constructor(filePath: String) {
        charMap = readCharMapFromFile(File(filePath))
    }

    private val charMap: Map<Char, Char>

    private fun readCharMapFromFile(file: File = DEFAULT_FILE): Map<Char, Char> =
        file.readLines().mapNotNull { line ->
            try {
                val chars = line.split(" ").map { it.trim() }.take(2).map { it.single() }
                if (chars.size == 2)
                    chars[0] to chars[1]
                else null
            } catch (e: Exception) {
                null
            }
        }.toMap()

    private fun checkDefaultFileExistsAndCreateIfNecessary() {
        if (!DEFAULT_FILE.exists()) {
            DEFAULT_FILE.parentFile.mkdirs()
            val letters = "abcdefghijklmnopqrstuvwxyz"
            val numbers = "0123456789"
            val lettersShuffled = letters.toCharArray().toList().shuffled()
            val numbersShuffled = numbers.toCharArray().toList().shuffled()

            DEFAULT_FILE.writeText(
                letters.mapIndexed { index, c -> "$c ${lettersShuffled[index]}" }.joinToString("\n") +
                        "\n" +
                        numbers.mapIndexed { index, c -> "$c ${numbersShuffled[index]}" }.joinToString("\n")
            )
        }
    }

    fun mapChar(char: Char): Char {
        if (char.isLetter()) {
            if (char.isLowerCase() && charMap.containsKey(char))
                return charMap[char]!!
            if (char.isUpperCase() && charMap.containsKey(char.toLowerCase()))
                return charMap[char.toLowerCase()]!!.toUpperCase()
        } else if (char.isDigit() && charMap.containsKey(char))
            return charMap[char]!!
        return char
    }

    companion

    object {
        val DEFAULT_FILE =
            if ("test".equals(System.getenv("KOTLIN_ENV"), true))
                File(".git_incognito/charmap")
            else
                File(System.getProperty("user.home")).resolve(".git_incognito/charmap")
    }
}

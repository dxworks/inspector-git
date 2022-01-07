package org.dxworks.inspectorgit.gitclient.incognito

import java.io.File
import java.nio.charset.Charset
import java.nio.charset.MalformedInputException
import java.nio.charset.StandardCharsets

val authorRegex = Regex("(author:)(.*)")
val emailRegex = Regex("(email:)(.*)(@.*)")

fun processLogfile(logFile: File, destination: File? = null, charset: Charset = Charsets.UTF_8) {
    val incognitoFile = logFile.resolveSibling("${logFile.nameWithoutExtension}-incognito.git")

    try {
        logFile.useLines(charset) { lines ->
            incognitoFile.bufferedWriter().use { writer ->
                lines.map {
                    authorRegex.find(it)?.let { match ->
                        match.groupValues[1] + encryptString(match.groupValues[2])
                    } ?: emailRegex.find(it)?.let { match ->
                        match.groupValues[1] + encryptString(match.groupValues[2]) + match.groupValues[3]
                    } ?: it
                }.forEach { writer.write("$it\n") }
            }
        }
    } catch (e: MalformedInputException) {
        if (charset == Charsets.UTF_8)
            processLogfile(logFile, charset = StandardCharsets.ISO_8859_1)
    }

    incognitoFile.copyTo(destination ?: logFile, true)
    incognitoFile.delete()
}

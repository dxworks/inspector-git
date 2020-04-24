import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
        val (oldFileName, newFileName) = extractFileNames(lines, type)
                    currentHunkLines.add("$it\n")
                else
                    currentHunkLines.add(currentHunkLines.removeAt(currentHunkLines.size - 1).dropLast(1))
    private fun extractFileNames(lines: List<String>, type: ChangeType): Pair<String, String> {
        val oldFilePrefix = if (type == ChangeType.RENAME) "rename from " else "--- a/"
        val newFilePrefix = if (type == ChangeType.RENAME) "rename to " else "+++ b/"

        val oldFileName = if (type == ChangeType.ADD) devNull else {
            extractFileName(lines, oldFilePrefix)
        }
        val newFileName = if (type == ChangeType.DELETE) devNull else {
            extractFileName(lines, newFilePrefix)
        }
        return Pair(oldFileName, newFileName)
    }

    private fun extractFileName(lines: List<String>, fileNamePrefix: String): String {
        val nameLine = lines.find { it.startsWith(fileNamePrefix) }
        return nameLine?.removePrefix(fileNamePrefix) ?: extractFileName(lines[0])
    }

    fun extractFileName(diffLine: String): String {
        val namesStartIndex = diffLine.indexOf(" a/") + 3
        val names = diffLine.substring(namesStartIndex)
        val namesParts = names.split(" b/")
        return namesParts.take(namesParts.size / 2).joinToString(" b/")
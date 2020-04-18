import org.dxworks.inspectorgit.gitclient.dto.ChangeDTO
        val (oldFileName, newFileName) = extractFileNames(lines)
                    currentHunkLines.add(it)
    private fun extractFileNames(lines: List<String>): Pair<String, String> {
        val oldFilePrefix = "--- a/"
        val newFilePrefix = "+++ b/"
        return Pair(lines.find { it.startsWith(oldFilePrefix) }!!.removePrefix(oldFilePrefix),
                lines.find { it.startsWith(newFilePrefix) }!!.removePrefix(newFilePrefix))
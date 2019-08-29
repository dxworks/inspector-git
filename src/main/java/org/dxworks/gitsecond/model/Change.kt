package org.dxworks.gitsecond.model


data class Change(var lines: List<Line>, var commit: Commit, var type: ChangeType, var file: File?, var oldFilename: String, var newFileName: String) {

    fun isRenameChange(): Boolean {
        return type == ChangeType.RENAME
    }
}

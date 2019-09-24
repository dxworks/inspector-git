package org.dxworks.inspectorgit.utils

class OsUtils {
    companion object {
        private val osName by lazy { System.getProperty("os.name") }
        val isWindows by lazy { osName.toLowerCase().contains("win") }
        val isUnix by lazy { !isWindows }
        val commandInterpreterPrefix by lazy { if (isUnix) "bash" else "cmd.exe" }
        val interpreterArg by lazy { if (isUnix) "-c" else "/C" }
    }
}
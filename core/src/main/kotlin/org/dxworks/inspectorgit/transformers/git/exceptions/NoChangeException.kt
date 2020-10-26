package org.dxworks.inspectorgit.transformers.git.exceptions

class NoChangeException(val fileName: String) : Exception("File $fileName does not exist!")

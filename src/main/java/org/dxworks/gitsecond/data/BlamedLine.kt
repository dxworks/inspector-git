package org.dxworks.gitsecond.data

import org.dxworks.gitsecond.model.AuthorID

data class BlamedLine(var line: String, var lineNumber: Int, var sourceAuthorID: AuthorID, var committerAuthorID: AuthorID, var commitId: String) {

}

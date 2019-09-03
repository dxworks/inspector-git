package org.dxworks.gitsecond.dto

import org.dxworks.gitsecond.model.AuthorID

class AnnotatedLineDTO(val commitId: String, val authorID: AuthorID, val number: Int, val content: String)

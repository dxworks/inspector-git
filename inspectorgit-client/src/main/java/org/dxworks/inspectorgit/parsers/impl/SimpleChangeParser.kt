package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.HunkDTO
import org.dxworks.inspectorgit.parsers.abstracts.ChangeParser

class SimpleChangeParser(otherCommitId: String) : ChangeParser(otherCommitId) {
    override fun addAnnotatedLines(changeDTO: ChangeDTO){}
}

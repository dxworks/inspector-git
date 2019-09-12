package org.dxworks.gitinspector.parsers.impl

import org.dxworks.gitinspector.dto.ChangeDTO
import org.dxworks.gitinspector.dto.HunkDTO
import org.dxworks.gitinspector.parsers.abstracts.ChangeParser

class SimpleChangeParser(otherCommitId: String) : ChangeParser(otherCommitId) {
    override fun addAnnotatedLines(changeDTO: ChangeDTO){}

}

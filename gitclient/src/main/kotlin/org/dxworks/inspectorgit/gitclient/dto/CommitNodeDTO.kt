package org.dxworks.inspectorgit.gitclient.dto

class CommitNodeDTO(val id: String,
                    val parents: List<CommitNodeDTO>) {
    val children: MutableList<CommitNodeDTO> = ArrayList()
    fun addChild(commitNodeDTO: CommitNodeDTO) {
        children.add(commitNodeDTO)
    }
}
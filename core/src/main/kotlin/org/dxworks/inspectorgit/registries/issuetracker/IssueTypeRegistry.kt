package org.dxworks.inspectorgit.registries.issuetracker

import org.dxworks.inspectorgit.model.issuetracker.IssueType
import org.dxworks.inspectorgit.registries.AbstractRegistry

class IssueTypeRegistry : AbstractRegistry<IssueType, String>() {
    override fun getID(entity: IssueType) = entity.id
}
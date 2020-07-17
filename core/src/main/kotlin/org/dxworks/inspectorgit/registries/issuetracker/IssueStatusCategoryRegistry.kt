package org.dxworks.inspectorgit.registries.issuetracker

import org.dxworks.inspectorgit.model.issuetracker.IssueStatusCategory
import org.dxworks.inspectorgit.registries.AbstractRegistry

class IssueStatusCategoryRegistry : AbstractRegistry<IssueStatusCategory, String>() {
    override fun getID(entity: IssueStatusCategory) = entity.key
}

package org.dxworks.inspectorgit.registries.issuetracker

import org.dxworks.inspectorgit.model.issuetracker.IssueStatus
import org.dxworks.inspectorgit.model.issuetracker.IssueStatusCategory
import org.dxworks.inspectorgit.registries.AbstractRegistry


class IssueStatusRegistry : AbstractRegistry<IssueStatus, String>() {
    override fun getID(entity: IssueStatus) = entity.id
    fun isNew(id: String) = hasCategory(id, IssueStatusCategory.new)
    fun isIndeterminate(id: String) = hasCategory(id, IssueStatusCategory.indeterminate)
    fun isDone(id: String) = hasCategory(id, IssueStatusCategory.done)

    private fun hasCategory(id: String, categoryKey: String) =
            getById(id)?.category?.key?.equals(categoryKey) ?: false
}
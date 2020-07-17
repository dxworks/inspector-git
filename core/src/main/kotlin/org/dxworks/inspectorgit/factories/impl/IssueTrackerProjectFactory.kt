package org.dxworks.inspectorgit.factories.impl

import org.dxworks.inspectorgit.factories.ProjectFactory
import org.dxworks.inspectorgit.jira.dtos.TaskImportDTO
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.transformers.IssueTrackerTransformer

class IssueTrackerProjectFactory : ProjectFactory {
    override fun create(dto: Any, name: String): Project? {
        if (dto is TaskImportDTO) {
            return IssueTrackerTransformer(dto.issueStatuses, dto.issueTypes, dto.users, dto.issues, name).transform()
        }
        return null
    }
}
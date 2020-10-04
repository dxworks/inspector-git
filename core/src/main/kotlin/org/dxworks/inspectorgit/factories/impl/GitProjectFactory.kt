package org.dxworks.inspectorgit.factories.impl

import org.dxworks.inspectorgit.factories.ProjectFactory
import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.transformers.GitProjectTransformer

class GitProjectFactory : ProjectFactory {
    override fun create(dto: Any, name: String): Project? {
        if (dto is GitLogDTO) {
            return GitProjectTransformer(dto, name).transform()
        }
        return null
    }
}

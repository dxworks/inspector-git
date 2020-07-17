package org.dxworks.inspectorgit.factories.impl

import org.dxworks.inspectorgit.factories.ProjectFactory
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.remote.dtos.RemoteInfoDTO
import org.dxworks.inspectorgit.transformers.RemoteGitTransformer

class RemoteGitProjectFactory : ProjectFactory {
    override fun create(dto: Any, name: String): Project? {
        if (dto is RemoteInfoDTO) {
            return RemoteGitTransformer(dto, name).transform()
        }
        return null
    }
}
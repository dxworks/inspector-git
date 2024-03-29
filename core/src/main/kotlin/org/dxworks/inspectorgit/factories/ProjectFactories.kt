package org.dxworks.inspectorgit.factories

import org.dxworks.inspectorgit.factories.impl.GitProjectFactory
import org.dxworks.inspectorgit.factories.impl.IssueTrackerProjectFactory
import org.dxworks.inspectorgit.factories.impl.RemoteGitProjectFactory
import org.dxworks.inspectorgit.model.Project

class ProjectFactories {
    companion object {
        private val gitProjectFactory = GitProjectFactory()
        private val projectFactories = listOf(gitProjectFactory, IssueTrackerProjectFactory(), RemoteGitProjectFactory())

        var computeAnnotatedLines
            get() = gitProjectFactory.computeAnnotatedLines
            set(value) {
                gitProjectFactory.computeAnnotatedLines = value
            }

        fun create(dto: Any, name: String): Project {
            return projectFactories.mapNotNull { it.create(dto, name) }.firstOrNull()
                    ?: throw UnsupportedOperationException("Project could not be crated")
        }
    }
}

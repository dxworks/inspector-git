package org.dxworks.inspectorgit.model.issuetracker

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueStatusCategoryRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueStatusRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueTypeRegistry

class IssueTrackerProject(override val name: String) : Project() {
    override val accountRegistry = AccountRegistry()
    val issueRegistry = IssueRegistry()
    val issueTypeRegistry = IssueTypeRegistry()
    val issueStatusRegistry = IssueStatusRegistry()
    val issueStatusCategoryRegistry = IssueStatusCategoryRegistry()
}


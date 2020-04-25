package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.model.Developer
import org.dxworks.inspectorgit.model.Project

class AccountMergeTool(private val project: Project) {
    fun mergeAll(accountMerges: List<AccountMerge>) = accountMerges.forEach { merge(it) }

    fun merge(accountMerge: AccountMerge) {
        val developer = Developer(accountMerge.name, accountMerge.accountIds.mapNotNull { project.accountRegistry.getById(it) })
        developer.accounts.forEach { it.delveloper = developer }
        project.developerRegistry.add(developer)
    }
}
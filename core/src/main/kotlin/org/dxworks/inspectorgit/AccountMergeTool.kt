package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.model.ComposedProject
import org.dxworks.inspectorgit.model.Developer

class AccountMergeTool(private val composedProject: ComposedProject) {
    fun mergeAll(accountMerges: List<AccountMerge>) = accountMerges.forEach { merge(it) }

    fun merge(accountMerge: AccountMerge) {
        val developer = Developer(accountMerge.name, accountMerge.accountIds.mapNotNull { composedProject.accountRegistry.getById(it) })
        developer.accounts.forEach { it.developer = developer }
        composedProject.developerRegistry.add(developer)
    }
}
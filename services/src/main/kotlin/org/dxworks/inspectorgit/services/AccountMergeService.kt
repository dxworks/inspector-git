package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.AccountMerge
import org.dxworks.inspectorgit.model.Account
import org.dxworks.inspectorgit.model.Developer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AccountMergeService(private val system: LoadedSystem) {
    companion object {
        private val LOG = LoggerFactory.getLogger(AnalysisService::class.java)
    }

    fun mergeAccounts(accountMerges: List<AccountMerge>) {
        accountMerges.forEach { accountMerge ->
            mergeAccounts(accountMerge)
        }
    }

    fun mergeAccounts(accountMerge: AccountMerge) {
        val name = accountMerge.name
        if (system.developerRegistry.contains(name)) {
            LOG.warn("Could not create developer $name. Already exists")
            return
        }

        val accountRegistries = system.projects.map { it.value.accountRegistry }
        val accounts = accountMerge.idsToMerge.flatMap {
            val accounts = accountRegistries.mapNotNull { registry -> registry.getById(it) }
            if (accounts.isEmpty())
                LOG.warn("Could not find any account with id $it")
            accounts
        }
        val alreadyMergedAccounts = accounts.filter { it.developer != null }

        if (accounts.isEmpty()) {
            LOG.warn("Could not create developer $name. No accounts found")
            return
        }

        if (alreadyMergedAccounts.isNotEmpty()) {
            val sb = StringBuilder()
            sb.appendln("Could not create developer $name. The following accounts are already merged:")
            alreadyMergedAccounts.forEach { sb.appendln(it.id) }
            LOG.warn(sb.toString())
            return
        }

        createAndLinkDeveloper(name, accounts)
    }


    fun mergeDevelopers(devMerge: AccountMerge) {
        val name = devMerge.name

        val devs = devMerge.idsToMerge.mapNotNull {
            val dev = system.developerRegistry.getById(it)
            if (dev == null)
                LOG.warn("Could not find any developer with name $it")
            dev
        }

        if (devs.isEmpty()) {
            LOG.warn("Could not create developer $name. No accounts found")
            return
        }

        val accounts = devs.flatMap { it.accounts }
        devs.forEach { system.developerRegistry.remove(it.name) }

        if (system.developerRegistry.contains(name)) {
            LOG.warn("Could not create developer $name. Already exists")
            return
        }

        createAndLinkDeveloper(name, accounts)
    }

    private fun createAndLinkDeveloper(name: String, accounts: List<Account>) {
        val dev = Developer(name, accounts)
        system.developerRegistry.add(dev)
        dev.accounts.forEach { it.developer = dev }
    }

    fun mergeDevelopers(devMerges: List<AccountMerge>) {
        devMerges.forEach(this::mergeDevelopers)
    }

    fun clearDevelopers() {
        system.developerRegistry.all.forEach { it.accounts.forEach { it.developer = null } }
        system.developerRegistry.clear()
    }
}

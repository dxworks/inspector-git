package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.AccountMerge
import org.dxworks.inspectorgit.services.AccountMergeService
import org.dxworks.inspectorgit.services.chronos.ChronosSettingsService
import org.dxworks.inspectorgit.web.apiPath
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$apiPath/accountMerge")
class AccountMergeController(private val accountMergeService: AccountMergeService,
                             private val chronosSettingsService: ChronosSettingsService) {

    @PutMapping("accounts")
    fun mergeAccounts(@RequestBody accountMerges: List<AccountMerge>) {
        accountMergeService.mergeAccounts(accountMerges)
    }

    @PutMapping("developers")
    fun mergeDevelopers(@RequestBody accountMerges: List<AccountMerge>) {
        accountMergeService.mergeDevelopers(accountMerges)
    }

    @GetMapping("applyChronosMerges")
    fun applyChronosMerges() {
        chronosSettingsService.applyMerges()
    }
}

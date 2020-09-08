package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.AccountMerge
import org.dxworks.inspectorgit.services.impl.AccountMergeService
import org.dxworks.inspectorgit.web.apiPath
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$apiPath/accountMerge")
class AccountMergeController(private val accountMergeService: AccountMergeService) {

    @PutMapping("accounts")
    fun mergeAccounts(@RequestBody accountMerges: List<AccountMerge>) {
        accountMergeService.mergeAccounts(accountMerges)
    }

    @PutMapping("developers")
    fun mergeDevelopers(@RequestBody accountMerges: List<AccountMerge>) {
        accountMergeService.mergeDevelopers(accountMerges)
    }
}
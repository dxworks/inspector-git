package org.dxworks.inspectorgit.services.chronos

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dxworks.inspectorgit.AccountMerge
import org.dxworks.inspectorgit.services.AccountMergeService
import org.dxworks.inspectorgit.services.LoadedSystem
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getSystemFolder
import org.dxworks.inspectorgit.utils.chronosSettingsFileName
import org.springframework.stereotype.Service

@Service
class ChronosSettingsService(private val loadedSystem: LoadedSystem,
                             private val accountMergeService: AccountMergeService) {

    fun getAccountMerges(): List<AccountMerge> {
        val chronosSettingsFile = getSystemFolder(loadedSystem.id).toPath().resolve(chronosSettingsFileName).toFile()

        return jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }.readValue<ChronosAuthorsWrapper>(chronosSettingsFile).authors.toAccountMerges()
    }

    fun applyMerges() {
        accountMergeService.mergeAccounts(getAccountMerges())
    }


}
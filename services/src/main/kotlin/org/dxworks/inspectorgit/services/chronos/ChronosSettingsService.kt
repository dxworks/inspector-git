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
import java.io.File

@Service
class ChronosSettingsService(private val loadedSystem: LoadedSystem,
                             private val accountMergeService: AccountMergeService) {

    fun getAccountMerges(file: File): List<AccountMerge> {
        return jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }.readValue<ChronosAuthorsWrapper>(file).authors.toAccountMerges()
    }

    fun applyMerges(file: File = getSystemFolder(loadedSystem.id).toPath().resolve(chronosSettingsFileName).toFile()) {
        accountMergeService.mergeAccounts(getAccountMerges(file))
    }
}

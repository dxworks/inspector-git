package org.dxworks.inspectorgit.services.impl.chronos

import org.dxworks.inspectorgit.AccountMerge

class ChronosAuthorsWrapper(val authors: ChronosAliasesWrapper)

class ChronosAliasesWrapper(var aliases: List<ChronosAuthorMerge>) {
    fun toAccountMerges() = aliases.map { it.toAccountMerge() }
}

class ChronosAuthorMerge(private val name: String, private val aliases: List<ChronosAuthorId>) {
    fun toAccountMerge() = AccountMerge(name, aliases.map(ChronosAuthorId::id))
}


class ChronosAuthorId(private val name: String, private val email: String) {
    val id: String
        get() = "$name <$email>"
}


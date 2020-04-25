package org.dxworks.inspectorgit.model

class Developer(val name: String,
                var accounts: List<Account>) {
    inline fun <reified T : Account> getAccountsOfType() =
            accounts.filterIsInstance<T>()
}
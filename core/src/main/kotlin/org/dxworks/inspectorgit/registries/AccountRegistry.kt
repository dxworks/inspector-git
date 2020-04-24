package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.Account

class AccountRegistry : AbstractRegistry<Account, String>() {
    override fun getID(entity: Account) = entity.id
}
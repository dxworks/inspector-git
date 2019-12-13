package org.dxworks.inspectorgit.pullrequests.miners

import java.util.*

data class PRMinerOptions(val username: String,
                          val password: String,
                          val owner: String,
                          val repository: String,
                          val newerThan: Date? = null)
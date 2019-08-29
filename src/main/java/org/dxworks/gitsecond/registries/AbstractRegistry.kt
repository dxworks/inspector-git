package org.dxworks.gitsecond.registries

import java.util.*

abstract class AbstractRegistry<TYPE, ID> {
    private val map: MutableMap<ID, TYPE> = HashMap()

    val all: List<TYPE>
        get() = map.values as List<TYPE>

    val allIDs: Set<ID>
        get() = map.keys


    fun getByID(id: ID): TYPE? {
        return map[id]
    }

    fun add(entity: TYPE, id: ID): TYPE? {
        return map.put(id, entity)
    }

    fun add(entity: TYPE): TYPE? {
        return map.put(getID(entity), entity)
    }

    protected abstract fun getID(entity: TYPE): ID
}

package org.dxworks.inspectorgit.core.registries

import java.util.*

abstract class AbstractRegistry<TYPE, ID> {
    private val map: MutableMap<ID, TYPE> = HashMap()

    val all: Collection<TYPE>
        get() = map.values

    val allIDs: Set<ID>
        get() = map.keys

    open fun getById(id: ID): TYPE? {
        return map[id]
    }

    open fun contains(id: ID): Boolean {
        return map.contains(id)
    }

    fun add(entity: TYPE, id: ID): TYPE? {
        return map.put(id, entity)
    }

    fun add(entity: TYPE): TYPE? {
        return map.put(getID(entity), entity)
    }

    fun addAll(entities: Collection<TYPE>) {
        entities.forEach { map[getID(it)] = it }
    }

    fun remove(id: ID): TYPE? {
        return map.remove(id)
    }

    fun delete(entity: TYPE): TYPE? {
        return map.remove(getID(entity))
    }

    protected abstract fun getID(entity: TYPE): ID
}

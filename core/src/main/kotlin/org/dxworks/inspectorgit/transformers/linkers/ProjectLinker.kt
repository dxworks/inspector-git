package org.dxworks.inspectorgit.transformers.linkers

import org.dxworks.inspectorgit.model.Project
import kotlin.reflect.KClass

interface ProjectLinker<A : Project, B : Project> {
    fun link(a: A, b: B)
    fun getKey(): Pair<KClass<A>, KClass<B>>
}

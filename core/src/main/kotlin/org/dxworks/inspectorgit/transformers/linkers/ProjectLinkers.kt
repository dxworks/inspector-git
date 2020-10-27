package org.dxworks.inspectorgit.transformers.linkers

import org.dxworks.inspectorgit.model.Project
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class ProjectLinkers {
    val linkers: Map<Pair<KClass<*>, KClass<*>>, ProjectLinker<out Project, out Project>> =
            HashMap<Pair<KClass<*>, KClass<*>>, ProjectLinker<out Project, out Project>>().apply {
                val gitIssueLinker = GitIssueLinker()
                val gitRemoteLinker = GitRemoteLinker()
                val issueRemoteLinker = IssueRemoteLinker()
                put(gitIssueLinker.getKey(), gitIssueLinker)
                put(gitRemoteLinker.getKey(), gitRemoteLinker)
                put(issueRemoteLinker.getKey(), issueRemoteLinker)
            }

    inline fun <reified A : Project, reified B : Project> link(a: A, b: B) {
        if (a.isLinked(b) || b.isLinked(a))
            return
        linkers[Pair(A::class, B::class)]
                ?.also { (it as ProjectLinker<A, B>).link(a, b) }
                ?: linkers[Pair(B::class, A::class)]
                        ?.also { (it as ProjectLinker<B, A>).link(b, a) }
                        ?.also {
                            a.link(b)
                            b.link(a)
                        } ?: println("NO Linker found for ${A::class.jvmName} to ${B::class.jvmName}")
    }
}

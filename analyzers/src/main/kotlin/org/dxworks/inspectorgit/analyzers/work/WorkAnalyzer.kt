package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.api.configuration.AbstractConfigurable
import org.dxworks.inspectorgit.api.configuration.exceptions.NotConfiguredException
import org.dxworks.inspectorgit.client.enums.LineOperation
import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.Change
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.Project
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.HashMap

@Component
class WorkAnalyzer : AbstractConfigurable<WorkAnalyzerConfiguration>() {
    override fun setConfiguration(properties: Properties): WorkAnalyzerConfiguration {
        return WorkAnalyzerConfiguration(properties)
    }

    private var results: MutableMap<Commit, WorkAnalyzerResult> = HashMap()

    fun analyze(project: Project): Collection<WorkAnalyzerResult> {
        if (!configured) throw NotConfiguredException(this.javaClass.simpleName)

        results = HashMap()
        project.commitRegistry.all.sortedBy { it.committerDate }.map { analyze(it) }
        return results.values
    }

    private fun analyze(commit: Commit) {
        commit.changes.forEach { change ->
            val currentResult = results[commit] ?: WorkAnalyzerResult(commit)

            val addedLines = getChangesOfType(change, LineOperation.ADD)
            val removedLines = getChangesOfType(change, LineOperation.REMOVE)

            val brandNewWork = addedLines.filter { removedLines.none { removedLine -> removedLine.number == it.number } }

            val codeChangingWork = addedLines.subtract(brandNewWork).map { CodeChange(it, getReplacedLine(removedLines, it)) }
            val legacyRefactor = codeChangingWork.filter { it.removedLine.commit.olderThan(configuration.legacyCodeAge) }

            val recentChanges = codeChangingWork.filter { !it.removedLine.commit.olderThan(configuration.recentWorkPeriod) }
            val helpOthers = recentChanges.filter { it.removedLine.commit.author != commit.author }

            val recentRemovedLines = removedLines.filter { !it.commit.olderThan(configuration.recentWorkPeriod) }
            recentRemovedLines.forEach {
                val result = results[it.commit]!!
                result.newWork.remove(it)
                result.churn.add(it)
            }
            currentResult.newWork.addAll(brandNewWork.toMutableList())
            currentResult.legacyRefactor.addAll(legacyRefactor.toMutableList())
            currentResult.helpOthers.addAll(helpOthers.toMutableList())
            results[commit] = currentResult
        }
    }

    private fun getReplacedLine(removedLines: List<AnnotatedLine>, addedLine: AnnotatedLine) =
            removedLines.find { removedLine -> removedLine.number == addedLine.number }!!

    private fun getChangesOfType(change: Change, operation: LineOperation) =
            change.lineChanges.filter { it.operation == operation }.map { it.annotatedLine }
}
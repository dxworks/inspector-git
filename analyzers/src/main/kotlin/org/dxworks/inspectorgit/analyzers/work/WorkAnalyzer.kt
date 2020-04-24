package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.api.configuration.AbstractConfigurable
import org.dxworks.inspectorgit.api.configuration.exceptions.NotConfiguredException
import org.dxworks.inspectorgit.gitclient.enums.LineOperation
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.AnnotatedLine
import org.dxworks.inspectorgit.model.git.Commit
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

            val (deleteLineChanges, addLineChanges) = change.lineChanges.partition { it.operation == LineOperation.DELETE }
            val addedLines = addLineChanges.map { AnnotatedLine(it.number, it.content) }
            val deletedLines = deleteLineChanges.map { AnnotatedLine(it.number, it.content) }

            val brandNewWork = addedLines.filter { deletedLines.none { removedLine -> removedLine.number == it.number } }

            val codeChangingWork = addedLines.subtract(brandNewWork).map { CodeChange(it, getReplacedLine(deletedLines, it)) }
            val legacyRefactor = codeChangingWork.filter { it.removedLine.content.commit.olderThan(configuration.legacyCodeAge, it.addedLine.content.commit) }

            val recentChanges = codeChangingWork.filter { !it.removedLine.content.commit.olderThan(configuration.recentWorkPeriod, it.addedLine.content.commit) }
            val helpOthers = recentChanges.filter { it.removedLine.content.commit.author != commit.author }

            val recentRemovedLines = recentChanges.map { it.removedLine }
            recentRemovedLines.forEach {
                val result = results[it.content.commit]!!
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

}
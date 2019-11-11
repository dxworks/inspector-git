package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.api.configuration.AbstractConfigurable
import org.dxworks.inspectorgit.api.configuration.exceptions.NotConfiguredException
import org.dxworks.inspectorgit.client.dto.ProjectDTO
import org.dxworks.inspectorgit.client.enums.LineOperation
import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.Change
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.JsonUtils
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

fun main() {
    val workAnalyzer = WorkAnalyzer()
    val properties = Properties()
    properties.setProperty("recentWorkPeriod", "2m")
    properties.setProperty("legacyCodeAge", "3m")
    workAnalyzer.configure(properties)
    val project = ProjectTransformer(JsonUtils.jsonFromFile(FileSystemUtils.getDtoFilePathFor("kafka", "trunk"), ProjectDTO::class.java), "kafka").transform()
    val results = workAnalyzer.analyze(project)
    print("New work: ")
    println(results.map { it.newWork.size }.toIntArray().sum())
    print("Legacy refactor: ")
    println(results.map { it.legacyRefactor.size }.toIntArray().sum())
    print("Help Others: ")
    println(results.map { it.helpOthers.size }.toIntArray().sum())
    print("Churn: ")
    println(results.map { it.churn.size }.toIntArray().sum())
}
package org.dxworks.inspectorgit.registries.issuetracker

import org.dxworks.inspectorgit.model.issuetracker.DetailedIssue
import org.dxworks.inspectorgit.model.issuetracker.Issue
import org.dxworks.inspectorgit.model.issuetracker.Period
import org.dxworks.inspectorgit.registries.AbstractRegistry
import java.time.LocalTime


class IssueRegistry : AbstractRegistry<Issue, String>() {
    override fun getId(entity: Issue) = entity.id

    val allDetailedIssues: List<DetailedIssue>
        get() = all.filter { it is DetailedIssue }.map { it as DetailedIssue }

    val period: Period
        get() = Period(
                allDetailedIssues.minBy { it.created }!!.created.with(LocalTime.MIN),
                allDetailedIssues.maxBy { it.updated }!!.updated.with(LocalTime.MAX)
        )
}
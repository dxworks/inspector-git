import org.dxworks.inspectorgit.services.impl.LoadedSystem

import java.time.temporal.ChronoUnit

LoadedSystem system

resultsPerDay = system.projects.collect {
    def proj = it.value

    def summaryPerDay = proj.commitRegistry.all.groupBy {
        it.authorDate.truncatedTo(ChronoUnit.DAYS)
    }.collect {
        def commits = it.value
        [
                day    : it.key.toString(),
                files  : commits.changes.flatten().newFileName.unique().size(),
                commits: commits.size(),
                hunks  : commits.changes.flatten().hunks.flatten().size(),
                growth : commits.changes.flatten().hunks.flatten().sum { it.addedLines.size() - it.deletedLines.size() },
                churn  : commits.changes.flatten().hunks.flatten().sum { it.addedLines.size() + it.deletedLines.size() },
        ]
    }

    [proj: it.key, summary: summaryPerDay]

//    def commitsPerMonth = proj.commitRegistry.all.groupBy {
//        it.authorDate.truncatedTo(ChronoUnit.MONTHS)
//    }
}

export(resultsPerDay, 'DailySummary')

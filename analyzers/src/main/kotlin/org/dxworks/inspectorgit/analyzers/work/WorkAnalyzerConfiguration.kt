package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.api.configuration.Configuration
import java.time.Period

class WorkAnalyzerConfiguration(configuration: Map<String, String>) : Configuration() {
    private val recentWorkPeriodField = "recentWorkPeriod"
    private val legacyCodeAgeField = "legacyCodeAge"
    private val periodRegex = Regex("^[1-9]+[0-9]*([dwmy])\$")

    override val validation = mapOf(Pair(recentWorkPeriodField, periodRegex), Pair(legacyCodeAgeField, periodRegex))


    init {
        validate(configuration)
    }


    val recentWorkPeriod: Period = parsePeriod(configuration.getOrElse(recentWorkPeriodField) { throw IllegalStateException() })

    val legacyCodeAge: Period = parsePeriod(configuration.getOrElse(legacyCodeAgeField) { throw IllegalStateException() })

    private fun parsePeriod(period: String): Period {
        val unit = period.last()
        val amount = period.substring(0, period.length - 1).toInt()

        return when (unit) {
            'd' -> Period.ofDays(amount)
            'w' -> Period.ofWeeks(amount)
            'm' -> Period.ofMonths(amount)
            'y' -> Period.ofYears(amount)
            else -> throw java.lang.IllegalStateException()
        }
    }
}
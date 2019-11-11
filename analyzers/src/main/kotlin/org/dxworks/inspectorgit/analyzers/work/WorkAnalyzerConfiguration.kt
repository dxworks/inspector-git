package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.api.configuration.Configuration
import org.dxworks.inspectorgit.api.configuration.validators.RegexValidator
import java.time.Period
import java.util.*

class WorkAnalyzerConfiguration(configuration: Properties) : Configuration {
    private val recentWorkPeriodField = "recentWorkPeriod"
    private val legacyCodeAgeField = "legacyCodeAge"
    private val periodRegex = Regex("^[1-9]+[0-9]*([dwmy])\$")

    init {
        validate(configuration, listOf(RegexValidator(listOf(recentWorkPeriodField, legacyCodeAgeField), periodRegex)))
    }


    val recentWorkPeriod: Period = parsePeriod(configuration.getProperty(recentWorkPeriodField))

    val legacyCodeAge: Period = parsePeriod(configuration.getProperty(legacyCodeAgeField))

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
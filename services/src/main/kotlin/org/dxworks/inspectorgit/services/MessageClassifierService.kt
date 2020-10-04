package org.dxworks.inspectorgit.services

import org.springframework.stereotype.Service

@Service
class MessageClassifierService {
    companion object {
        val bugFixingWords: List<String> = listOf("korr", "bugfix", "hotfix", "fix", "bug", "error", "issue", "fehler", "korrektur", "correct", "korrigier", "workaround", "crash", "mistake", "wrong", "typo", "broken", "crash")
        val refactoringWords: List<String> = listOf("aufräumen", "aufgeräumt", "sanitize", "sanitise", "refact", "restructur", "clean", "cleanup", "rename", "rework", "umbennant", "formatierung", "reorgani", "reinig")
        val refinementWords: List<String> = listOf("task", "improve", "update", "remove", "review", "change", "angepasst", "aktualisiert", "anpassung", "entfernt", "modifi", "geändert", "adapt", "improved", "adjust", "performance", "refin", "hack", "work-around")
        val newWorkWords: List<String> = listOf("new", "erweiter", "hinzugefügt", "add", "added", "feat")
        val categories = mapOf(
                "bug" to bugFixingWords,
                "refactoring" to refactoringWords,
                "refinement" to refinementWords,
                "newWork" to newWorkWords
        )
    }

    fun classify(message: String): Set<String> {
        return categories.filter { message.split(Regex("[^a-zA-z]*")).any(it.value::contains) }.keys
    }

}

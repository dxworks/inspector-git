package org.dxworks.inspectorgit.transformers

fun getRegexWithWordBoundaryGroups(middleRegexGroupContent: String) = "(^|\b|\\W)($middleRegexGroupContent)(\b|\\W|$)".toRegex()
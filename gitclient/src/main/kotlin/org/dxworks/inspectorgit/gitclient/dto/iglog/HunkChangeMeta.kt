package org.dxworks.inspectorgit.gitclient.dto.iglog

open class HunkChangeMeta(
        val addedContentMeta: ContentMeta,
        val deletedContentMeta: ContentMeta,
        val unmodifiedContentMeta: ContentMeta
)
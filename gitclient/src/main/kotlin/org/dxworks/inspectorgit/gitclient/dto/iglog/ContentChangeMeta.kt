package org.dxworks.inspectorgit.gitclient.dto.iglog

open class ContentChangeMeta(
        val addedContentMeta: ContentMeta,
        val deletedContentMeta: ContentMeta,
        val unmodifiedContentMeta: ContentMeta
)
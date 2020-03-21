package org.dxworks.inspectorgit.gitclient.extractors.impl

import com.github.difflib.DiffUtils
import com.github.difflib.patch.Chunk
import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkType
import org.dxworks.inspectorgit.gitclient.dto.gitlog.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.iglog.ContentMeta
import org.dxworks.inspectorgit.gitclient.dto.iglog.HunkChangeMeta
import org.dxworks.inspectorgit.gitclient.extractors.MetaExtractor

class HunkChangeMetaExtractor : MetaExtractor<HunkChangeMeta>() {
    private val contentMetaSplitter = "-"
    private val hunkMetaSplitter = " "
    override val linePrefix: String
        get() = "~>"

    override fun extract(hunkDTO: HunkDTO): String {
        val (addContentMeta, deleteContentMeta) = if (hunkDTO.type == HunkType.MODIFY) {
            val patch = DiffUtils.diff(getTextAsList(hunkDTO.deletedLineChanges), getTextAsList(hunkDTO.addedLineChanges))
            if (patch.deltas.isEmpty())
                Pair(ContentMeta(0, 0), ContentMeta(0, 0))
            else
                patch.deltas.map { Pair(getContentMeta(it.target), getContentMeta(it.source)) }
                        .reduce { acc, pair -> Pair(acc.first + pair.first, acc.second + pair.second) }
        } else {
            getAddAndDeleteContentMeta(hunkDTO)
        }
        val unmodifiedContentMeta = getUnmodifiedContentMeta(deleteContentMeta, addContentMeta, hunkDTO)

        return getFormattedLine(HunkChangeMeta(deleteContentMeta, addContentMeta, unmodifiedContentMeta))
    }

    private fun getAddAndDeleteContentMeta(hunkDTO: HunkDTO): Pair<ContentMeta, ContentMeta> {
        return Pair(getContentMetaFromLineChange(hunkDTO.addedLineChanges), getContentMetaFromLineChange(hunkDTO.deletedLineChanges))
    }

    private fun getContentMetaFromLineChange(lineChanges: List<LineChangeDTO>): ContentMeta {
        return getContentMeta(getTextAsList(lineChanges))
    }

    private fun getUnmodifiedContentMeta(deleteContentMeta: ContentMeta, addContentMeta: ContentMeta, hunkDTO: HunkDTO): ContentMeta {
        return if (deleteContentMeta.isEmpty() || addContentMeta.isEmpty()) {
            ContentMeta(0, 0)
        } else {
            val textAsList = getTextAsList(hunkDTO.deletedLineChanges)
            val oldContentMeta = getContentMeta(textAsList)
            oldContentMeta - deleteContentMeta
        }
    }

    private fun getContentMeta(chunk: Chunk<Char>) =
            getContentMeta(chunk.lines)

    private fun getContentMeta(chars: List<Char>) = ContentMeta(chars.size, chars.count { it.isWhitespace() })


    private fun getTextAsList(lineChanges: List<LineChangeDTO>) =
            lineChanges.joinToString { it.content }.toList()


    private fun getFormattedLine(hunkChangeMeta: HunkChangeMeta): String {
        return "${getFormattedContentMeta(hunkChangeMeta.addedContentMeta)}$hunkMetaSplitter${getFormattedContentMeta(hunkChangeMeta.deletedContentMeta)}$hunkMetaSplitter${getFormattedContentMeta(hunkChangeMeta.unmodifiedContentMeta)}"
    }


    private fun getFormattedContentMeta(contentMeta: ContentMeta): String {
        return "${contentMeta.totalChars}$contentMetaSplitter${contentMeta.spaces}"
    }


    override fun parse(line: String): HunkChangeMeta {
        val changeMetaStrings = line.split(hunkMetaSplitter)
        return HunkChangeMeta(
                addedContentMeta = getContentMetaFromString(changeMetaStrings[0]),
                deletedContentMeta = getContentMetaFromString(changeMetaStrings[1]),
                unmodifiedContentMeta = getContentMetaFromString(changeMetaStrings[2])
        )
    }

    private fun getContentMetaFromString(contentMetaString: String): ContentMeta {
        val fields = contentMetaString.split(contentMetaSplitter)
        return ContentMeta(fields[0].toInt(), fields[1].toInt())
    }
}

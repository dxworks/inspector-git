package org.dxworks.inspectorgit.gitclient.extractors.impl

import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.LineOperation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LineOperationsMetaExtractorTest {

    private val lineOperationsMetaExtractor = LineOperationsMetaExtractor()

    @Test
    fun readModify() {
        val hunkDTO = HunkDTO(listOf(
                LineChangeDTO(LineOperation.ADD, 4, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.ADD, 5, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.ADD, 6, "Lorem ipsum dolor sit amet"),

                LineChangeDTO(LineOperation.ADD, 10, "Lorem ipsum dolor sit amet"),

                LineChangeDTO(LineOperation.ADD, 14, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.ADD, 15, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.ADD, 16, "Lorem ipsum dolor sit amet"),

                LineChangeDTO(LineOperation.DELETE, 1, "Lorem ipsum dolor sit amet"),

                LineChangeDTO(LineOperation.DELETE, 4, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.DELETE, 5, "Lorem ipsum dolor sit amet"),

                LineChangeDTO(LineOperation.DELETE, 12, "Lorem ipsum dolor sit amet"),

                LineChangeDTO(LineOperation.DELETE, 21, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.DELETE, 22, "Lorem ipsum dolor sit amet"),

                LineChangeDTO(LineOperation.DELETE, 40, "Lorem ipsum dolor sit amet")
        ))
        val line = lineOperationsMetaExtractor.write(hunkDTO)

        val read = lineOperationsMetaExtractor.read(line)

        assertEquals(3, read.addRanges.size)

        assertEquals(4, read.addRanges[0].first)
        assertEquals(6, read.addRanges[0].second)

        assertEquals(10, read.addRanges[1].first)
        assertEquals(10, read.addRanges[1].second)

        assertEquals(14, read.addRanges[2].first)
        assertEquals(16, read.addRanges[2].second)


        assertEquals(5, read.deleteRanges.size)

        assertEquals(1, read.deleteRanges[0].first)
        assertEquals(1, read.deleteRanges[0].second)

        assertEquals(4, read.deleteRanges[1].first)
        assertEquals(5, read.deleteRanges[1].second)

        assertEquals(12, read.deleteRanges[2].first)
        assertEquals(12, read.deleteRanges[2].second)

        assertEquals(21, read.deleteRanges[3].first)
        assertEquals(22, read.deleteRanges[3].second)

        assertEquals(40, read.deleteRanges[4].first)
        assertEquals(40, read.deleteRanges[4].second)
    }

    @Test
    fun readAdd() {
        val hunkDTO = HunkDTO(listOf(
                LineChangeDTO(LineOperation.ADD, 2, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.ADD, 5, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.ADD, 6, "Lorem ipsum dolor sit amet")
        ))
        val line = lineOperationsMetaExtractor.write(hunkDTO)

        val read = lineOperationsMetaExtractor.read(line)

        assertEquals(2, read.addRanges.size)

        assertEquals(2, read.addRanges[0].first)
        assertEquals(2, read.addRanges[0].second)

        assertEquals(5, read.addRanges[1].first)
        assertEquals(6, read.addRanges[1].second)
    }

    @Test
    fun readDelete() {
        val hunkDTO = HunkDTO(listOf(
                LineChangeDTO(LineOperation.DELETE, 12, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.DELETE, 13, "Lorem ipsum dolor sit amet"),
                LineChangeDTO(LineOperation.DELETE, 30, "Lorem ipsum dolor sit amet")
        ))

        val line = lineOperationsMetaExtractor.write(hunkDTO)

        val read = lineOperationsMetaExtractor.read(line)

        assertEquals(2, read.deleteRanges.size)

        assertEquals(12, read.deleteRanges[0].first)
        assertEquals(13, read.deleteRanges[0].second)

        assertEquals(30, read.deleteRanges[1].first)
        assertEquals(30, read.deleteRanges[1].second)
    }
}
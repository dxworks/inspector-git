package org.dxworks.inspectorgit.parsers.impl


import org.dxworks.inspectorgit.client.parsers.impl.MergeHunkParser
import org.junit.jupiter.api.Test

class MergeHunkParserTest {
    @Test
    fun testParse() {
        val hunkDTO = MergeHunkParser(1, 2).parse(getLines2())
        print(hunkDTO)
    }

    private fun getLines() = listOf(
            "@@@ -102,14 -157,8 +112,13 @@@",
            "-           if(!file.canRead)",
            "-             throw new IOException(\"Could not read file \" + file)",
            " -          val offset = filename.substring(0, filename.length - LogFileSuffix.length).toLong",
            " -          // TODO: we should ideally rebuild any missing index files, instead of erroring out",
            " -          if(!Log.indexFilename(dir, offset).exists)",
            " -            throw new IllegalStateException(\"Found log file with no corresponding index file.\")",
            " -          logSegments.add(new LogSegment(dir = dir, ",
            " -                                         startOffset = offset,",
            " -                                         indexIntervalBytes = indexIntervalBytes, ",
            " -                                         maxIndexSize = maxIndexSize))",
            "++          // if its a log file, load the corresponding log segment",
            " +          val start = filename.substring(0, filename.length - LogFileSuffix.length).toLong",
            " +          val hasIndex = Log.indexFilename(dir, start).exists",
            " +          val segment = new LogSegment(dir = dir, ",
            " +                                       startOffset = start,",
            " +                                       indexIntervalBytes = indexIntervalBytes, ",
            " +                                       maxIndexSize = maxIndexSize)",
            " +          if(!hasIndex) {",
            " +            // this can only happen if someone manually deletes the index file",
            " +            error(\"Could not find index file corresponding to log file %s, rebuilding index...\".format(segment.log.file.getAbsolutePath))",
            " +            segment.recover(maxMessageSize)",
            " +          }",
            " +          logSegments.put(start, segment)"
    )

    private fun getLines2() = listOf(
            "@@@ -65,3 -65,3 +71,6 @@@ log4j.additivity.kafka.controller=fals",
            " +log4j.logger.kafka.log.LogCleaner=INFO, cleanerAppender",
            " +log4j.additivity.kafka.log.LogCleaner=false",
            " +log4j.logger.kafka.log.Cleaner=INFO, cleanerAppender",
            " +log4j.additivity.kafka.log.Cleaner=false",
            "++",
            "+ log4j.logger.state.change.logger=TRACE, stateChangeAppender",
            "+ log4j.additivity.state.change.logger=false",
            " -",
            " -"
    )
}
/*
 * Copyright 2015 Mikhail Shugay (mikhail.shugay@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antigenomics.juncstat.genomic

import com.antigenomics.juncstat.Parser
import com.antigenomics.juncstat.Range

class GenomicInfoProvider {
    final static int binSize = 100_000
    final Map<String, List<Transcript>> transcriptMap
    final int transcriptsCount

    GenomicInfoProvider(InputStream plainTextTableIS, Parser<Transcript> parser) {
        this.transcriptMap = new HashMap<>()
        int transcriptsCount = 0
        plainTextTableIS.splitEachLine(parser.sep) {
            if (!it[0].startsWith(parser.comment)) {
                def transcript = parser.parse(it)

                if (transcript.coding) {
                    bin(transcript.chr, transcript).each { // transcript can span several bins
                        def transcriptList = transcriptMap[it]
                        if (transcriptList == null) {
                            transcriptMap.put(it, transcriptList = new ArrayList<Transcript>())
                        }
                        transcriptList.add(transcript)
                    }

                    transcriptsCount++
                }
            }
        }

        this.transcriptsCount = transcriptsCount
    }

    List<Transcript> getTranscripts(String chr, Range range) {
        def bins = bin(chr, range)

        bins.collect { bin ->
            transcriptMap[bin]?.findAll { range.inside(it) } ?: []
        }.flatten()
    }

    static List<String> bin(String chr, Range range) {
        int bin1 = range.start / binSize, bin2 = (range.end - 1) / binSize

        bin1 == bin2 ? [chr + bin1] : [chr + bin1, chr + bin2]
    }
}

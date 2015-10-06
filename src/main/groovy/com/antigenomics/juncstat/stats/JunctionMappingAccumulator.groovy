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

package com.antigenomics.juncstat.stats

import com.antigenomics.juncstat.genomic.GenomicInfoProvider
import com.antigenomics.juncstat.genomic.Transcript
import com.antigenomics.juncstat.mapping.MappedJunction

import java.util.concurrent.atomic.AtomicLong

class JunctionMappingAccumulator {
    final Map<Transcript, TranscriptCounters> transcriptCounters = new HashMap<>()
    final AtomicLong mappedJunctionsCounter = new AtomicLong(), totalMappingsCounter = new AtomicLong()

    JunctionMappingAccumulator(GenomicInfoProvider genomicInfoProvider) {
        Map<String, AtomicLong> geneCounters = new HashMap<>()

        genomicInfoProvider.transcriptMap.values().each {
            it.each {
                def geneCounter = geneCounters[it.geneId]
                if (geneCounter == null) {
                    geneCounters.put(it.geneId, geneCounter = new AtomicLong())
                }
                transcriptCounters.put(it, new TranscriptCounters(geneCounter))
            }
        }
    }

    void update(MappedJunction mappedJunction) {
        int score = mappedJunction.junction.score

        mappedJunction.mappings.each { mapping ->
            // only same transcript as of now
            transcriptCounters[mapping.exon1.parent].with {
                geneCounter.addAndGet(score)
                transcriptCounter.addAndGet(score)
                if (mapping.outOfFrame) {
                    outOfFrameCounter.addAndGet(score)
                }
            }
            totalMappingsCounter.incrementAndGet()
        }

        if (!mappedJunction.mappings.empty) {
            mappedJunctionsCounter.incrementAndGet()
        }
    }

    List<GeneStats> collectGeneStats() {
        transcriptCounters.entrySet().groupBy { it.key.geneId }.
                collect {
                    String geneId = it.key
                    List<Map.Entry<Transcript, TranscriptCounters>> entries = it.value

                    long totalCount = entries.first().value.geneCount, oofCount = 0
                    double weightedOofCount = 0
                    int expressedTranscripts = 0, totalTranscripts = entries.size()

                    int exonCount = 0
                    entries.each {
                        int transcriptCount = it.value.transcriptCount
                        weightedOofCount += it.value.outOfFrameCount * it.value.transcriptCount
                        oofCount += it.value.outOfFrameCount
                        if (transcriptCount > 0) {
                            expressedTranscripts++
                        }
                        exonCount = Math.max(exonCount, it.key.exons.size())
                    }

                    if (weightedOofCount > 0) {
                        weightedOofCount /= totalCount
                    }

                    new GeneStats(geneId,
                            totalCount, (long) weightedOofCount, oofCount,
                            expressedTranscripts, totalTranscripts,
                            exonCount
                    )
                }
    }

    long getMappedJunctionsCount
    {
        mappedJunctionsCounter.get()
    }
    
    long getTotalMappingsCount
    {
        totalMappingsCounter.get()
    }
}

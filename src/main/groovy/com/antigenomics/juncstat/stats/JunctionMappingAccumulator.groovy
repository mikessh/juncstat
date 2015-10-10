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
import com.antigenomics.juncstat.mapping.MappedJunction

import java.util.concurrent.atomic.AtomicLong

class JunctionMappingAccumulator {
    final Map<String, GeneStats> countersByGene = new HashMap<>()
    final AtomicLong mappedJunctionsCounter = new AtomicLong(), totalMappingsCounter = new AtomicLong()

    JunctionMappingAccumulator(GenomicInfoProvider genomicInfoProvider) {

        genomicInfoProvider.transcriptMap.values().each {
            it.each {
                def geneStats = countersByGene[it.geneId]
                if (geneStats == null) {
                    countersByGene.put(it.geneId, geneStats = new GeneStats())
                }
                geneStats.add(it)
            }
        }
    }

    void update(MappedJunction mappedJunction) {
        int score = mappedJunction.junction.score

        mappedJunction.mappings.each { mapping ->
            def geneId = mapping.exon1.parent.geneId
            countersByGene[geneId].with {
                it.scoreCounter.addAndGet(score)
                it.mappingCounter.incrementAndGet()
                if (mapping.outOfFrame) {
                    it.oofMappingCounter.incrementAndGet()
                }
            }

            totalMappingsCounter.incrementAndGet()
        }

        if (mappedJunction.mapped) {
            mappedJunctionsCounter.incrementAndGet()
        }
    }

    /*
    List<GeneStats> collectGeneStats() {
        countersByGene.entrySet().groupBy { it.key.geneId }.
                collect {
                    String geneId = it.key
                    List<Map.Entry<Transcript, TranscriptCounter>> entries = it.value

                    long totalCount = 0, oofCount = 0
                    double weightedOofCount = 0
                    int expressedTranscripts = 0, totalTranscripts = entries.size()

                    int exonCount = 0
                    entries.each {
                        int transcriptCount = it.value.count
                        weightedOofCount += it.value.outOfFrameCount * transcriptCount
                        oofCount += it.value.outOfFrameCount
                        totalCount += transcriptCount
                        if (transcriptCount > 0) {
                            expressedTranscripts++
                        }
                        exonCount = Math.max(exonCount, it.key.exons.size())
                    }

                    if (weightedOofCount > 0) {
                        weightedOofCount /= totalCount
                    }

                    new GeneStats(geneId,
                            geneCounters[geneId].get(), totalCount, (long) weightedOofCount, oofCount,
                            expressedTranscripts, totalTranscripts,
                            exonCount
                    )
                }
    }*/

    long getMappedJunctionsCount
    {
        mappedJunctionsCounter.get()
    }

    long getTotalMappingsCount
    {
        totalMappingsCounter.get()
    }
}

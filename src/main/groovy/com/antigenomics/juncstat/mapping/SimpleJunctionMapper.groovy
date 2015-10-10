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

package com.antigenomics.juncstat.mapping

import com.antigenomics.juncstat.genomic.GenomicInfoProvider
import com.antigenomics.juncstat.genomic.Transcript

class SimpleJunctionMapper implements JunctionMapper {
    final GenomicInfoProvider genomicInfoProvider

    SimpleJunctionMapper(GenomicInfoProvider genomicInfoProvider) {
        this.genomicInfoProvider = genomicInfoProvider
    }

    @Override
    MappedJunction map(Junction junction) {
        def mappings = new ArrayList<Mapping>()
        def transcripts1 = new HashSet(genomicInfoProvider.getTranscripts(junction.chr, junction.range1)),
            transcripts2 = new HashSet(genomicInfoProvider.getTranscripts(junction.chr, junction.range2))

        transcripts1.each { Transcript transcript1 ->
            transcripts2.each { Transcript transcript2 ->
                if (transcript1.geneId == transcript2.geneId && // no chimeric junctions
                        junction.strand == transcript1.strand) {
                    def exon1 = transcript1.exons.find { it.overlap(junction.range1) },
                        exon2 = transcript2.exons.find { it.overlap(junction.range2) }

                    if (exon1 && exon2) {
                        mappings << (transcript1.strand ? new Mapping(exon1, exon2) : new Mapping(exon2, exon1))
                    }
                }
            }
        }

        new MappedJunction(mappings, junction)
    }
}

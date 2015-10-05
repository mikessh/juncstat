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
        // no chimeric junctions
        def transcripts1 = new HashSet(genomicInfoProvider.getTranscripts(junction.chr, junction.range1)),
            transcripts2 = new HashSet(genomicInfoProvider.getTranscripts(junction.chr, junction.range2))

        transcripts1.retainAll(transcripts2)

        transcripts1.each { Transcript transcript ->
            // same transcript mappings only
            def exon1 = transcript.exons.find { it.overlap(junction.range1) },
                exon2 = transcript.exons.find { it.overlap(junction.range1) }

            if (exon1 && exon2) {
                mappings << new Mapping(exon1, exon2)
            }
        }
        
        new MappedJunction(mappings, junction)
    }
}

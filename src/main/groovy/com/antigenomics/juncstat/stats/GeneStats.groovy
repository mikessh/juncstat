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

import com.antigenomics.juncstat.genomic.Exon
import com.antigenomics.juncstat.genomic.Transcript

import java.util.concurrent.atomic.AtomicInteger

class GeneStats {
    final AtomicInteger scoreCounter = new AtomicInteger(),
                        mappingCounter = new AtomicInteger(),
                        oofMappingCounter = new AtomicInteger()
    final Set<Exon> exons = new HashSet<>()
    final List<Transcript> transcripts = new ArrayList<>()

    GeneStats() {
    }

    void add(Transcript transcript) {
        transcripts.add(transcript)
        exons.addAll(transcript.exons)
    }

    int getCodingExonCount() {
        exons.findAll { it.coding }.size()
    }

    int getScore() {
        scoreCounter.get()
    }

    int getMappings() {
        mappingCounter.get()
    }

    int getOofMappings() {
        oofMappingCounter.get()
    }
}

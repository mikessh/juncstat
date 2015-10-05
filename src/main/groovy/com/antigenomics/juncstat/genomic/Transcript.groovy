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

import com.antigenomics.juncstat.Range

class Transcript extends Range {
    final String chr, id, geneId
    final int cdsStart, cdsEnd
    final boolean strand
    final List<Exon> exons = new ArrayList<>()

    Transcript(String chr, String id, String geneId, int start, int end, int cdsStart, int cdsEnd, boolean strand) {
        super(start, end)
        this.chr = chr
        this.id = id
        this.geneId = geneId
        this.cdsStart = cdsStart
        this.cdsEnd = cdsEnd
        this.strand = strand
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Transcript that = (Transcript) o

        if (id != that.id) return false

        return true
    }

    int hashCode() {
        return id.hashCode()
    }
}

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

import com.antigenomics.juncstat.Range

class Junction {
    final String id, chr
    final Range range1, range2
    final boolean strand
    final int score

    Junction(String id, String chr, Range range1, Range range2, boolean strand, int score) {
        this.id = id
        this.chr = chr
        this.range1 = range1
        this.range2 = range2
        this.strand = strand
        this.score = score
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Junction junction = (Junction) o

        if (id != junction.id) return false

        return true
    }

    int hashCode() {
        return id.hashCode()
    }
}

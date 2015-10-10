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

class Exon extends Range {
    final int frame, remainder
    final Transcript parent

    Exon(int start, int end, Transcript parent, int frame) {
        super(start, end)
        this.parent = parent
        this.frame = frame
        this.remainder = frame >= 0 ? (end -
                start - 3 + frame // start of first complete codon
        ) % 3 : -1
    }

    boolean isCoding() {
        parent.isCoding() ? this.overlap(parent.cds) : false
    }
}

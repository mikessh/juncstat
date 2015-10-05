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

import com.antigenomics.juncstat.genomic.Exon

class Mapping {
    final Exon exon1, exon2
    final int frameDifference

    Mapping(Exon exon1, Exon exon2) {
        this.exon1 = exon1
        this.exon2 = exon2
        this.frameDifference = exon1.frame >= 0 && exon2.frame >= 0 ?
                (exon1.frame + exon2.frame) % 3 : 0
    }

    boolean isOutOfFrame() {
        frameDifference != 0
    }
}
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
    final String chr
    final Range range1, range2
    final boolean frame
    final int score

    Junction(String chr, Range range1, Range range2, boolean frame, int score) {
        this.chr = chr
        this.range1 = range1
        this.range2 = range2
        this.frame = frame
        this.score = score
    }
}

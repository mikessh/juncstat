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

package com.antigenomics.juncstat.parser

import com.antigenomics.juncstat.Parser
import com.antigenomics.juncstat.genomic.Exon
import com.antigenomics.juncstat.genomic.Transcript

class EnsGeneParser implements Parser<Transcript> {
    String sep = "\t", comment = "#"

    @Override
    Transcript parse(List<String> row) {
        //  0 bin
        //  1 name
        //  2 chrom
        //  3 strand
        //  4 txStart
        //  5 txEnd
        //  6 cdsStart
        //  7 cdsEnd
        //  8 exonCount
        //  9 exonStarts      
        // 10 exonEnds        
        // 11 score   
        // 12 name2   
        // 13 cdsStartStat    
        // 14 cdsEndStat      
        // 15 exonFrames
        def transcript = new Transcript(row[2], row[1], row[12],
                row[4].toInteger(), row[5].toInteger(),
                row[6].toInteger(), row[7].toInteger(), row[3] == "+")

        def starts = row[9].split(","),
            ends = row[10].split(","),
            frames = row[15].split(",")

        (0..<starts.length).each {
            transcript.exons << new Exon(
                    starts[it].toInteger(),
                    ends[it].toInteger(),
                    transcript,
                    frames[it].toInteger())
        }

        transcript
    }
}

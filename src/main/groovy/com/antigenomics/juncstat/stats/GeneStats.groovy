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

class GeneStats {
    final String geneId
    final long totalCount, weightedOutOfFrameCount, totalOutOfFrameCount
    final int expressedTranscripts, totalTranscripts, exonCount

    GeneStats(String geneId,
              long totalCount, long weightedOutOfFrameCount, long totalOutOfFrameCount,
              int expressedTranscripts, int totalTranscripts, int exonCount) {
        this.geneId = geneId
        this.totalCount = totalCount
        this.weightedOutOfFrameCount = weightedOutOfFrameCount
        this.totalOutOfFrameCount = totalOutOfFrameCount
        this.expressedTranscripts = expressedTranscripts
        this.totalTranscripts = totalTranscripts
        this.exonCount = exonCount
    }

    static
    final String HEADER = "gene.id\ttotal.count\t" +
            "weighted.out.of.frame.count\ttotal.out.of.frame.count\texpressed.transcripts\ttotal.transcripts\t" +
            "exon.count"

    @Override
    String toString() {
        [geneId, totalCount, weightedOutOfFrameCount, totalOutOfFrameCount, expressedTranscripts, totalTranscripts, exonCount].join("\t")
    }
}

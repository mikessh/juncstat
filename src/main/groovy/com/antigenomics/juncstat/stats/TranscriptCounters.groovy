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

import java.util.concurrent.atomic.AtomicLong

class TranscriptCounters {
    final AtomicLong geneCounter,
                     transcriptCounter = new AtomicLong(),
                     outOfFrameCounter = new AtomicLong()

    TranscriptCounters(AtomicLong geneCounter) {
        this.geneCounter = geneCounter
    }

    long getGeneCount() {
        geneCounter.get()
    }

    long getTranscriptCount() {
        transcriptCounter.get()
    }

    long getOutOfFrameCount() {
        outOfFrameCounter.get()
    }
}

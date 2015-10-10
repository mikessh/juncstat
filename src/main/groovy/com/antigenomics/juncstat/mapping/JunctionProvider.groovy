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

import com.antigenomics.juncstat.MathUtil
import com.antigenomics.juncstat.Parser

class JunctionProvider {

    static List<Junction> load(InputStream plainTextTableIS, Parser<Junction> parser) {
        def junctions = new ArrayList<>(100000)
        plainTextTableIS.splitEachLine(parser.sep) {
            if (!it[0].startsWith(parser.comment)) {
                junctions << parser.parse(it)
            }
        }
        junctions
    }

    static List<Junction> downSample(List<Junction> junctions, int size) {
        int totalScore = (int) junctions.sum { it.score }

        if (totalScore < size) {
            return new ArrayList<>(junctions)
        }

        println totalScore

        def flattenedJunctions = new Junction[totalScore]
        int counter = 0
        junctions.each {
            for (int i = 0; i < it.score; i++) {
                flattenedJunctions[counter++] = it
            }
        }

        MathUtil.shuffle(flattenedJunctions)

        def countMap = new HashMap<Junction, Integer>()

        for (int i = 0; i < size; i++) {
            def junction = flattenedJunctions[i]
            countMap.put(junction, (countMap[junction] ?: 0) + 1)
        }

        countMap.collect {
            def junction = it.key, score = it.value
            new Junction(junction.id, junction.chr, junction.range1, junction.range2, junction.strand, score)
        }
    }
}
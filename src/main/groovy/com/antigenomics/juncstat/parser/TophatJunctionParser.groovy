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
import com.antigenomics.juncstat.Range
import com.antigenomics.juncstat.mapping.Junction

class TophatJunctionParser implements Parser<Junction> {
    final String sep = "\t", comment = "track name"

    @Override
    Junction parse(List<String> row) {
        // 0     1       2       3            4 5 6       7       8       9 10    11
        // chr20 9353709 9360718 JUNC00000552 2 + 9353709 9360718 255,0,0 2 42,18 0,6991
        def overhangs = row[10].split(",").collect { it.toInteger() }
        new Junction(row[0],
                new Range(row[1].toInteger(), row[1].toInteger() + overhangs[0]),
                new Range(row[2].toInteger() - overhangs[1], row[2].toInteger()),
                row[5] == "+",
                row[4].toInteger()
        )
    }
}

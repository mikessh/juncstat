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

package com.antigenomics.juncstat

class Range {
    final int start, end

    Range(int start, int end) {
        this.start = start
        this.end = end
    }

    boolean inside(Range other) {
        other.start <= start && other.end >= end
    }

    boolean contains(int pos) {
        pos >= start && pos < end
    }

    boolean overlap(Range other) {
        start <= other.end && other.start <= end
    }

    int size() {
        end - start
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Range range = (Range) o

        if (end != range.end) return false
        if (start != range.start) return false

        return true
    }

    int hashCode() {
        int result
        result = start
        result = 31 * result + end
        return result
    }
}

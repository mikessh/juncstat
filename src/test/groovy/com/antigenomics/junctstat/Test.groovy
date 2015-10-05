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

package com.antigenomics.junctstat

import com.antigenomics.juncstat.genomic.GenomicInfoProvider
import com.antigenomics.juncstat.mapping.JunctionList
import com.antigenomics.juncstat.mapping.SimpleJunctionMapper
import com.antigenomics.juncstat.parser.EnsGeneParser
import com.antigenomics.juncstat.parser.TophatJunctionParser

class Test {
    @org.junit.Test
    void test() {
        def juncTxt =
                "chr1\t172213805\t172215109\tJUNC00006886\t2215\t-\t172213805\t172215109\t255,0,0\t2\t46,98\t0,1206\n" +
                        "chr1\t172215043\t172215550\tJUNC00006887\t5153\t-\t172215043\t172215550\t255,0,0\t2\t99,74\t0,433\n" +
                        "chr1\t172215476\t172216007\tJUNC00006888\t1309\t-\t172215476\t172216007\t255,0,0\t2\t74,99\t0,432\n" +
                        "chr1\t172215922\t172216892\tJUNC00006889\t1853\t-\t172215922\t172216892\t255,0,0\t2\t98,99\t0,871\n" +
                        "chr1\t172216795\t172217974\tJUNC00006890\t1954\t-\t172216795\t172217974\t255,0,0\t2\t99,85\t0,1094"
        def geneTxt =
                "1898\tENSMUST00000003554\tchr1\t-\t172209893\t172219868\t172210344\t172219690\t11\t172209893,172211947,172213027,172213336,172213805,172215011,172215476,172215908,172216793,172217889,172219411,\t172210503,172212022,172213128,172213391,172213851,172215142,172215550,172216020,172216894,172217974,172219868,\t0\tENSMUSG00000007122\tcmpl\tcmpl\t0,0,1,0,2,0,1,0,1,0,0,\n" +
                        "1898\tENSMUST00000170638\tchr1\t-\t172209894\t172213955\t172213955\t172213955\t5\t172209894,172211947,172213027,172213336,172213805,\t172210503,172212022,172213128,172213391,172213955,\t0\tENSMUSG00000007122\tnone\tnone\t-1,-1,-1,-1,-1,\n" +
                        "1898\tENSMUST00000170700\tchr1\t-\t172215139\t172219715\t172215139\t172219690\t4\t172215139,172215476,172215908,172219411,\t172215142,172215550,172216020,172219715,\t0\tENSMUSG00000007122\tincmpl\tincmpl\t0,1,0,0,"

        def juncStream = new ByteArrayInputStream(juncTxt.getBytes()),
            geneStream = new ByteArrayInputStream(geneTxt.getBytes())

        def giProvider = new GenomicInfoProvider(geneStream, new EnsGeneParser())
        def juncList = new JunctionList(juncStream, new TophatJunctionParser())
        
        def mapper = new SimpleJunctionMapper(giProvider)
        
        juncList.junctions.each {
            assert !mapper.map(it).mappings.empty
        }
    }
}

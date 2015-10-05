package com.antigenomics.juncstat

import com.antigenomics.juncstat.genomic.GenomicInfoProvider
import com.antigenomics.juncstat.mapping.JunctionList
import com.antigenomics.juncstat.mapping.SimpleJunctionMapper
import com.antigenomics.juncstat.parser.EnsGeneParser
import com.antigenomics.juncstat.parser.TophatJunctionParser
import com.antigenomics.juncstat.stats.GeneStats
import com.antigenomics.juncstat.stats.JunctionMappingAccumulator
import groovyx.gpars.GParsPool

def genomicFile = args[0], junctionsFile = args[1], outputFile = args[2]

println "Loading genomic info"
def genomicInfoProvider = new GenomicInfoProvider(Util.getStream(genomicFile), new EnsGeneParser())
def mapper = new SimpleJunctionMapper(genomicInfoProvider)
def accumulator = new JunctionMappingAccumulator(genomicInfoProvider)

println "Loading junctions"
def junctionList = new JunctionList(Util.getStream(junctionsFile), new TophatJunctionParser())

println "Mapping and counting"
GParsPool.withPool Runtime.runtime.availableProcessors(), {
    junctionList.junctions.eachParallel {
        accumulator.update(mapper.map(it))
    }
}

println "Summarizing"
def geneStats = accumulator.collectGeneStats()

println "Writing output"
new File(outputFile).withPrintWriter { pw ->
    pw.println(GeneStats.HEADER)
    geneStats.each {
        pw.println(it.toString())
    }
}
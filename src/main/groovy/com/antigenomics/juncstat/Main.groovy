package com.antigenomics.juncstat

import com.antigenomics.juncstat.genomic.GenomicInfoProvider
import com.antigenomics.juncstat.mapping.JunctionProvider
import com.antigenomics.juncstat.mapping.SimpleJunctionMapper
import com.antigenomics.juncstat.parser.EnsGeneParser
import com.antigenomics.juncstat.parser.TophatJunctionParser
import com.antigenomics.juncstat.stats.GeneStats
import com.antigenomics.juncstat.stats.JunctionMappingAccumulator
import groovyx.gpars.GParsPool

import java.util.concurrent.atomic.AtomicLong

import static com.antigenomics.juncstat.Util.sout

def genomicFile = args[0], junctionsFile = args[1], outputFile = args[2]

int threadCount = Runtime.runtime.availableProcessors()

sout "Loading genomic info"
def genomicInfoProvider = new GenomicInfoProvider(Util.getStream(genomicFile), new EnsGeneParser())
def mapper = new SimpleJunctionMapper(genomicInfoProvider)
def accumulator = new JunctionMappingAccumulator(genomicInfoProvider)
sout "Loaded ${genomicInfoProvider.transcriptsCount} transcripts"

sout "Loading junctions"
def junctionList = JunctionProvider.load(Util.getStream(junctionsFile), new TophatJunctionParser())
sout "Loaded ${junctionList.size()} junctions"

sout "Mapping and counting"
def counter = new AtomicLong()
GParsPool.withPool threadCount, {
    junctionList.eachParallel {
        accumulator.update(mapper.map(it))

        int count = counter.incrementAndGet()
        if (count % 10000 == 0) {
            sout "Processed $count junctions of ${junctionList.size()}. " +
                    "Mapped ${accumulator.mappedJunctionsCounter}, ${accumulator.totalMappingsCounter} total mappings."

        }
    }
}

sout "Summarizing"
def geneStats = accumulator.collectGeneStats()
sout "Got statistics for ${geneStats.size()} genes"

sout "Writing output"
new File(outputFile).withPrintWriter { pw ->
    pw.println(GeneStats.HEADER)
    geneStats.each {
        pw.println(it.toString())
    }
}
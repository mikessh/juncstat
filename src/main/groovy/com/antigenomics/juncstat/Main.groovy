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

def cli = new CliBuilder(usage: "juncstat [options] " +
        "genomic_info junctions1,junctions2,... condition1,condition2,... output_prefix")

cli.s(args: 1, argName: "name",
        "Software used to generate junctions file. Allowed values: tophat. [default=tophat]")
cli.t(args: 1, argName: "name",
        "Transcript table format. Allowed values: ensGene. [default=ensGene]")

// Misc
cli.h("Display this help message")

// PARSE ARGUMENTS

def opt = cli.parse(args)

if (opt.h || opt == null) {
    cli.usage()
    System.exit(3)
}
if (opt.arguments().size() != 4) {
    println "[ERROR] Too few arguments provided"
    System.exit(3)
}

def genomicFile = opt.arguments()[0],
    junctionFiles = opt.arguments()[1].split(","),
    conditions = opt.arguments()[2].split(","),
    outputPrefix = args[3]

if (junctionFiles.size() != conditions.size()) {
    println "[ERROR] Number of input files and conditions should match"
    System.exit(3)
}

def software = opt.s ?: "tophat", tableFormat = opt.t ?: "ensgene",
    threadCount = Runtime.runtime.availableProcessors()

def transcriptTableParser = null, junctionsFileParser = null

switch (tableFormat.toLowerCase()) {
    case "ensgene":
        transcriptTableParser = new EnsGeneParser()
        break
    default:
        println "[ERROR] Unrecognized transcript table format $tableFormat"
        System.exit(3)
}

switch (software.toLowerCase()) {
    case "tophat":
        junctionsFileParser = new TophatJunctionParser()
        break
    default:
        println "[ERROR] Unrecognized junctions file format $tableFormat"
        System.exit(3)
}

sout "Loading genomic info"
def genomicInfoProvider = new GenomicInfoProvider(Util.getStream(genomicFile), transcriptTableParser)
def mapper = new SimpleJunctionMapper(genomicInfoProvider)
def accumulator = new JunctionMappingAccumulator(genomicInfoProvider)
sout "Loaded ${genomicInfoProvider.transcriptsCount} transcripts"


new File(outputPrefix + ".gene.txt").withPrintWriter { pw ->
    
    pw.println("condition\tsample\t" + GeneStats.HEADER)
    
    junctionFiles.eachWithIndex { String sample, int i ->

        sout "Loading junctions from $sample"
        def junctionList = JunctionProvider.load(Util.getStream(sample), junctionsFileParser)
        sout "Loaded ${junctionList.size()} junctions"

        sout "Mapping and counting"
        def counter = new AtomicLong()
        GParsPool.withPool threadCount, {
            junctionList.eachParallel {
                accumulator.update(mapper.map(it))

                int count = counter.incrementAndGet()
                if (count % 100000 == 0) {
                    sout "Processed $count junctions of ${junctionList.size()}. " +
                            "Mapped ${accumulator.mappedJunctionsCounter}, ${accumulator.totalMappingsCounter} total mappings."
                }
            }
        }

        sout "Finished processing ${junctionList.size()} junctions. " +
                "Mapped ${accumulator.mappedJunctionsCounter}, ${accumulator.totalMappingsCounter} total mappings."

        sout "Summarizing"
        def geneStats = accumulator.collectGeneStats()
        sout "Got statistics for ${geneStats.size()} genes"

        sout "Writing output"
        def condition = conditions[i]
        geneStats.each {
            pw.println(condition + "\t" + sample + "\t" + it.toString())
        }
    }
}

sout "Done"
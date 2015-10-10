package com.antigenomics.juncstat

import com.antigenomics.juncstat.genomic.GenomicInfoProvider
import com.antigenomics.juncstat.mapping.JunctionProvider
import com.antigenomics.juncstat.mapping.MappedJunction
import com.antigenomics.juncstat.mapping.SimpleJunctionMapper
import com.antigenomics.juncstat.parser.EnsGeneParser
import com.antigenomics.juncstat.parser.TophatJunctionParser
import com.antigenomics.juncstat.stats.JunctionMappingAccumulator
import groovyx.gpars.GParsPool

import static com.antigenomics.juncstat.Util.sout

def cli = new CliBuilder(usage: "juncstat [options] " +
        "genomic_info junctions1,junctions2,... condition1,condition2,... output_prefix")

cli.s(args: 1, argName: "name",
        "Software used to generate junctions file. Allowed values: tophat. [default=tophat]")
cli.t(args: 1, argName: "name",
        "Transcript table format. Allowed values: ensGene. [default=ensGene]")
cli.x(args: 1, argName: "score",
        "Normalize junction list to a given total score. [default=1000000]")

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
    outputPrefix = opt.arguments()[3]

if (junctionFiles.size() != conditions.size()) {
    println "[ERROR] Number of input files and conditions should match"
    System.exit(3)
}

def software = opt.s ?: "tophat", tableFormat = opt.t ?: "ensgene",
    size = (opt.x ?: "5000000").toInteger(),
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
sout "Loaded ${genomicInfoProvider.transcriptsCount} transcripts"


new File(outputPrefix + ".genestat.txt").withPrintWriter { pw ->
    // pw.println("condition\tsample\tscore\tmappings\toof.mapping")
    pw.println("condition\tsample\tgene\tscore\tmappings\toof.mappings\tcoding.exons\ttranscripts")

    junctionFiles.eachWithIndex { String sample, int i ->
        def accumulator = new JunctionMappingAccumulator(genomicInfoProvider)

        sout "Loading junctions from $sample"
        def junctionList = JunctionProvider.load(Util.getStream(sample), junctionsFileParser)
        sout "Loaded ${junctionList.size()} junctions, down-sampling to $size"
        junctionList = JunctionProvider.downSample(junctionList, size)

        GParsPool.withPool threadCount, {
            sout "Mapping"
            Collection<MappedJunction> mappedJunctions = junctionList.collectParallel { mapper.map(it) }

            sout "Estimating gene level stats"
            mappedJunctions.eachParallel { accumulator.update(it) }

            sout "Finished processing ${junctionList.size()} junctions. " +
                    "Mapped ${accumulator.mappedJunctionsCounter}, ${accumulator.totalMappingsCounter} total mappings."

            sout "Estimating OOF scores and writing output"
            def condition = conditions[i]

            /*
            mappedJunctions.each { MappedJunction mj ->
                int score = mj.junction.score, mappings = 0, oofMappings = 0
                mj.mappings.each {
                    mappings++
                    if (it.outOfFrame) {
                        oofMappings++
                    }
                }

                pw.println(condition + "\t" + sample + "\t" + score + "\t" + mappings + "\t" + oofMappings)
            }*/

            accumulator.countersByGene.each {
                pw.println([condition, sample, it.key,
                            it.value.score, it.value.mappings, it.value.oofMappings,
                            it.value.codingExonCount, it.value.transcripts.size()].join("\t"))
            }
        }
    }
}

sout "Done"
package net.woggioni.klevtree.benchmark

import net.woggioni.klevtree.LevTrie
import java.io.BufferedReader
import java.io.InputStreamReader
import net.woggioni.jwo.Chronometer

fun main() {
    val tree = LevTrie().apply {
        caseSensitive = false
        algorithm = LevTrie.Algorithm.DAMERAU_LEVENSHTEIN
    }

    BufferedReader(
        InputStreamReader(Chronometer::class.java.getResourceAsStream("/cracklib-small"))
    ).use { reader ->
        for(line in reader.lines()) {
            tree.add(line.asIterable())
        }
    }
    val chr = Chronometer()
    val keys = arrayOf("camel", "coriolis", "mattel", "cruzer", "cpoper", "roublesoot")

    for (ind in 0 until 50) {
        for (searchKey in keys) {
            tree.fuzzySearch(searchKey, 6)
        }
    }
    for (searchKey in keys) {
        val standing = tree.fuzzySearch(searchKey, 6)
        for (res in standing) {
            println("distance: ${res.second}\t wordkey: ${res.first}")
        }
        println()
    }
    System.out.printf("Elapsed time: %.3f s\n", chr.elapsed(Chronometer.UnitOfMeasure.SECONDS))
    println("++++++++++++ End benchmark ++++++++++++")
}

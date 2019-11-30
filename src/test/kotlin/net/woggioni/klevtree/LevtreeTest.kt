package net.woggioni.klevtree

import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader


class LevtreeTest {

    @Test
    fun foo() {
        val reader = BufferedReader(
            InputStreamReader(javaClass.getResourceAsStream("/cracklib-small")))
        val tree = LevTrie()
        tree.caseSensitive = false
        try {
            for(line in reader.lines()) {
                tree.add(line.asIterable())
            }
        } finally {
            reader.close()
        }
        println(tree.add("dailies"))
        var node = tree.search("dailies")
        println(node!!.linealDescendant())
        tree.remove("dailies")
        node = tree.search("dailies")
        println(node)
        tree.algorithm = ILevTrie.Algorithm.DAMERAU_LEVENSHTEIN
        val result = tree.fuzzySearch("daiiles", 5)
        println(result)
    }
}
package net.woggioni.klevtree

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LevtreeTest {

    @Test
    fun trivialTest() {
        val tree = LevTrie()
        tree.caseSensitive = false
        tree.algorithm = LevTrie.Algorithm.DAMERAU_LEVENSHTEIN
        val word = "dailies"
        run {
            val pair = tree.add(word)
            Assertions.assertTrue(pair.first)
            val node = tree.search(word)
            Assertions.assertNotNull(node)
            Assertions.assertEquals(
                word,
                node!!.linealDescendant().fold(StringBuilder(), StringBuilder::append).toString()
            )
            val result = tree.fuzzySearch(word, 5)
            Assertions.assertEquals(1, result.size)
            Assertions.assertEquals(word to 0, result[0])
        }
        run {
            tree.remove(word)
            val node = tree.search(word)
            Assertions.assertNull(node)
            val result = tree.fuzzySearch(word, 5)
            Assertions.assertEquals(0, result.size)
        }
    }


    private fun initLevTrie() : LevTrie {
        val words = listOf(
            "tired",
            "authorise",
            "exercise",
            "bloody",
            "ritual",
            "trail",
            "resort",
            "landowner",
            "navy",
            "captivate",
            "captivity",
            "north")
        return run {
            val res = LevTrie()
            words.forEach {res.add(it)}
            res
        }
    }

    @Test
    fun levenshteinDistanceTest() {
        val tree = initLevTrie()
        tree.caseSensitive = false
        tree.algorithm = LevTrie.Algorithm.LEVENSHTEIN
        run {
            val word = "fired"
            val result = tree.fuzzySearch(word, 4)
            Assertions.assertEquals(4, result.size)
            Assertions.assertEquals("tired" to 1, result[0])
        }
        run {
            val word = "tierd"
            val result = tree.fuzzySearch(word, 4)
            Assertions.assertEquals(4, result.size)
            Assertions.assertEquals("tired" to 2, result[0])
        }
        run {
            val word = "tierd"
            tree.remove("tired")
            val result = tree.fuzzySearch(word, 4)
            Assertions.assertEquals(4, result.size)
            Assertions.assertEquals("trail" to 4, result[0])
        }
    }

    @Test
    fun damerauLevenshteinDistanceTest() {
        val tree = initLevTrie()
        tree.caseSensitive = false
        tree.algorithm = LevTrie.Algorithm.DAMERAU_LEVENSHTEIN
        run {
            val word = "fired"
            val result = tree.fuzzySearch(word, 4)
            Assertions.assertEquals(4, result.size)
            Assertions.assertEquals("tired" to 1, result[0])
        }
        run {
            val word = "capitvate"
            val result = tree.fuzzySearch(word, 4)
            Assertions.assertEquals(4, result.size)
            Assertions.assertEquals("captivate" to 1, result[0])
            Assertions.assertEquals("captivity" to 3, result[1])
        }
        run {
            tree.remove("captivate")
            val word = "capitvate"
            val result = tree.fuzzySearch(word, 4)
            Assertions.assertEquals(4, result.size)
            Assertions.assertEquals("captivity" to 3, result[0])
        }
    }
}
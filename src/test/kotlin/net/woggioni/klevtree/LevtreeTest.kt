package net.woggioni.klevtree

import org.junit.Assert
import org.junit.Test


class LevtreeTest {

    @Test
    fun trivialTest() {
        val tree = LevTrie()
        tree.caseSensitive = false
        tree.algorithm = ILevTrie.Algorithm.DAMERAU_LEVENSHTEIN
        val word = "dailies"
        run {
            val pair = tree.add(word)
            Assert.assertTrue(pair.first)
            val node = tree.search(word)
            Assert.assertNotNull(node)
            Assert.assertEquals(
                word,
                node!!.linealDescendant().fold(StringBuilder(), StringBuilder::append).toString()
            )
            val result = tree.fuzzySearch(word, 5)
            Assert.assertEquals(1, result.size)
            Assert.assertEquals(word to 0, result[0])
        }
        run {
            tree.remove(word)
            val node = tree.search(word)
            Assert.assertNull(node)
            val result = tree.fuzzySearch(word, 5)
            Assert.assertEquals(0, result.size)
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
        tree.algorithm = ILevTrie.Algorithm.LEVENSHTEIN
        run {
            val word = "fired"
            val result = tree.fuzzySearch(word, 4)
            Assert.assertEquals(4, result.size)
            Assert.assertEquals("tired" to 1, result[0])
        }
        run {
            val word = "tierd"
            val result = tree.fuzzySearch(word, 4)
            Assert.assertEquals(4, result.size)
            Assert.assertEquals("tired" to 2, result[0])
        }
        run {
            val word = "tierd"
            tree.remove("tired")
            val result = tree.fuzzySearch(word, 4)
            Assert.assertEquals(4, result.size)
            Assert.assertEquals("trail" to 4, result[0])
        }
    }

    @Test
    fun damerauLevenshteinDistanceTest() {
        val tree = initLevTrie()
        tree.caseSensitive = false
        tree.algorithm = ILevTrie.Algorithm.DAMERAU_LEVENSHTEIN
        run {
            val word = "fired"
            val result = tree.fuzzySearch(word, 4)
            Assert.assertEquals(4, result.size)
            Assert.assertEquals("tired" to 1, result[0])
        }
        run {
            val word = "capitvate"
            val result = tree.fuzzySearch(word, 4)
            Assert.assertEquals(4, result.size)
            Assert.assertEquals("captivate" to 1, result[0])
            Assert.assertEquals("captivity" to 3, result[1])
        }
        run {
            tree.remove("captivate")
            val word = "capitvate"
            val result = tree.fuzzySearch(word, 4)
            Assert.assertEquals(4, result.size)
            Assert.assertEquals("captivity" to 3, result[0])
        }
    }
}
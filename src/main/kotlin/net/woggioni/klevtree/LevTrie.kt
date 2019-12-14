package net.woggioni.klevtree

import net.woggioni.jwo.tree.StackContext
import net.woggioni.jwo.tree.TreeNodeVisitor
import net.woggioni.jwo.tree.TreeWalker
import net.woggioni.klevtree.node.CharNode
import net.woggioni.klevtree.node.TrieNode

typealias LevNode = TrieNode<Char, IntArray>

interface ILevTrie : ICharTrie<IntArray> {

    interface DistanceCalculator {
        fun compute(keyChecker : Trie.Keychecker<Char>,
                    stack: List<StackContext<LevNode, Unit>>,
                    wordkey: String,
                    worstCase : Int) : TreeNodeVisitor.VisitOutcome
    }

    object LevenshteinDistanceCalculator : DistanceCalculator {
        override fun compute(keyChecker : Trie.Keychecker<Char>,
                             stack: List<StackContext<LevNode, Unit>>,
                             wordkey: String,
                             worstCase: Int) : TreeNodeVisitor.VisitOutcome {
            val previousStackElement = stack[stack.size - 2]
            val currentStackElement = stack.last()
            val previousRow : IntArray = previousStackElement.node.payload!!
            val currentRow : IntArray = currentStackElement.node.payload!!
            for (i in 1..wordkey.length) {
                if(keyChecker.check(wordkey[i - 1], currentStackElement.node.key)) {
                    currentRow[i] = previousRow[i - 1]
                } else {
                    currentRow[i] = Math.min(Math.min(currentRow[i - 1], previousRow[i -1]), previousRow[i]) + 1
                }
            }
            return if(worstCase >= 0 && worstCase <= currentRow.min()!!) {
                TreeNodeVisitor.VisitOutcome.SKIP
            } else {
                TreeNodeVisitor.VisitOutcome.CONTINUE
            }
        }
    }

    object DamerauLevenshteinDistanceCalculator : DistanceCalculator {
        override fun compute(keyChecker : Trie.Keychecker<Char>,
                             stack: List<StackContext<LevNode, Unit>>,
                             wordkey: String,
                             worstCase : Int) : TreeNodeVisitor.VisitOutcome {
            val pse = stack[stack.size - 2]
            val cse = stack.last()
            val prow : IntArray = pse.node.payload!!
            val crow : IntArray = cse.node.payload!!
            for (i in 1..wordkey.length) {
                if (keyChecker.check(wordkey[i - 1], cse.node.key)) {
                    crow[i] = prow[i - 1]
                } else {
                    crow[i] = Math.min(Math.min(crow[i - 1], prow[i - 1]), prow[i]) + 1
                }
                if (stack.size > 2 && i > 1 && keyChecker.check(wordkey[i - 2], cse.node.key)
                    && keyChecker.check(wordkey[i - 1], pse.node.key)) {
                    val ppse = stack[stack.size - 3]
                    val pprow: IntArray = ppse.node.payload!!
                    crow[i] = Math.min(crow[i], pprow[i - 2] + 1)
                }
            }
            return if(worstCase >= 0 && worstCase <= prow.min()!!) {
                TreeNodeVisitor.VisitOutcome.SKIP
            } else {
                TreeNodeVisitor.VisitOutcome.CONTINUE
            }
        }
    }

    enum class Algorithm {
        /**
         * Plain Levenshtein distance
         */
        LEVENSHTEIN,
        /**
         * Damerau-Levenshtein distance
         */
        DAMERAU_LEVENSHTEIN
    }

    var distanceCalculator : DistanceCalculator

    var algorithm : Algorithm
        get() {
            return when(distanceCalculator) {
                LevenshteinDistanceCalculator -> Algorithm.LEVENSHTEIN
                DamerauLevenshteinDistanceCalculator -> Algorithm.DAMERAU_LEVENSHTEIN
                else -> Algorithm.LEVENSHTEIN
            }
        }
        set(value) {
            when(value) {
                Algorithm.LEVENSHTEIN -> distanceCalculator = LevenshteinDistanceCalculator
                Algorithm.DAMERAU_LEVENSHTEIN -> distanceCalculator = DamerauLevenshteinDistanceCalculator
            }
        }

    fun fuzzySearch(word : String, maxResult: Int) : List<Pair<String, Int>> {
        val result = sortedSetOf<Pair<String, Int>>(compareBy({ it.second }, { it.first }))
        val requiredSize = word.length + 1
        val visitor = object: TreeNodeVisitor<LevNode, Unit> {
            override fun visitPre(stack: List<StackContext<LevNode, Unit>>): TreeNodeVisitor.VisitOutcome {
                val currentStackElement = stack.last()
                val currentNode = currentStackElement.node
                if(currentNode.payload == null ||
                    currentNode.payload!!.size < requiredSize) {
                    if(stack.size == 1) {
                        currentNode.payload = IntArray(requiredSize) { i -> i }
                    } else {
                        currentNode.payload = IntArray(requiredSize) { i -> if(i == 0) stack.size - 1 else 0 }
                    }
                }
                if(stack.size > 1) {
                    if(currentStackElement.node.key == null) {
                        val sb = StringBuilder()
                        for(c in currentStackElement.node.linealDescendant()) {
                            sb.append(c)
                        }
                        val candidate = sb.toString()
                        val distance = stack[stack.size - 2].node.payload!![word.length]
                        result.add(candidate to distance)
                        if(result.size > maxResult) {
                            result.remove(result.last())
                        }
                        return TreeNodeVisitor.VisitOutcome.SKIP
                    } else {
                        return distanceCalculator.compute(keyChecker, stack, word,
                            if(result.size == maxResult) result.last().second else -1)
                    }
                } else {
                    return TreeNodeVisitor.VisitOutcome.CONTINUE
                }
            }
        }
        val walker = TreeWalker<LevNode, Unit>(visitor)
        walker.walk(root)
        return result.toList()
    }
}

class LevTrie : ILevTrie {

    override val root: TrieNode<Char, IntArray> = CharNode(null)

    override val tails = mutableListOf<TrieNode<Char, IntArray>>()

    override var keyChecker: Trie.Keychecker<Char> = ICharTrie.CaseSensitiveKeyChecker()

    override var caseSensitive : Boolean = true
    set(value) {
        if(value) {
            keyChecker = ICharTrie.CaseSensitiveKeyChecker()
        } else {
            keyChecker = ICharTrie.CaseInsensitiveKeyChecker()
        }
        field = value
    }

    override var distanceCalculator : ILevTrie.DistanceCalculator = ILevTrie.LevenshteinDistanceCalculator
}
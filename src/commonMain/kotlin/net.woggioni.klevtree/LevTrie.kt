package net.woggioni.klevtree

import net.woggioni.klevtree.node.CharNode
import net.woggioni.klevtree.node.TrieNode
import net.woggioni.klevtree.tree.TreeNodeVisitor
import net.woggioni.klevtree.tree.TreeWalker

internal typealias LevNode = TrieNode<Char, IntArray>

class LevTrie : CharTrie<IntArray>() {

    override val root: TrieNode<Char, IntArray> = CharNode(null)

    override val tails = mutableListOf<TrieNode<Char, IntArray>>()

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

    private var distanceCalculator : DistanceCalculator = DistanceCalculator.LevenshteinDistanceCalculator

    var algorithm : Algorithm
        get() {
            return when(distanceCalculator) {
                DistanceCalculator.LevenshteinDistanceCalculator -> Algorithm.LEVENSHTEIN
                DistanceCalculator.DamerauLevenshteinDistanceCalculator -> Algorithm.DAMERAU_LEVENSHTEIN
            }
        }
        set(value) {
            distanceCalculator = when(value) {
                Algorithm.LEVENSHTEIN -> DistanceCalculator.LevenshteinDistanceCalculator
                Algorithm.DAMERAU_LEVENSHTEIN -> DistanceCalculator.DamerauLevenshteinDistanceCalculator
            }
        }

    fun fuzzySearch(word : String, maxResult: Int) : List<Pair<String, Int>> {
        val comparator : Comparator<Pair<String, Int>> = compareBy({ it.second }, { it.first })
        val result = mutableListOf<Pair<String, Int>>()
        val requiredSize = word.length + 1
        val visitor = object: TreeNodeVisitor<LevNode, Unit> {
            override fun visitPre(stack: List<TreeNodeVisitor.StackContext<LevNode, Unit>>): TreeNodeVisitor.VisitOutcome {
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
                        result.sortWith(comparator)
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
        val walker = TreeWalker(visitor)
        walker.walk(root)
        return result.toList()
    }
}
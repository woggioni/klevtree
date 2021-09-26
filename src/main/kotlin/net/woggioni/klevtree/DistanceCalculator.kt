package net.woggioni.klevtree

import net.woggioni.jwo.TreeNodeVisitor

sealed class DistanceCalculator {
    abstract fun compute(keyChecker : Trie.Keychecker<Char>,
                stack: List<TreeNodeVisitor.StackContext<LevNode, Unit>>,
                wordkey: String,
                worstCase : Int) : TreeNodeVisitor.VisitOutcome

    object LevenshteinDistanceCalculator : DistanceCalculator() {
        override fun compute(keyChecker : Trie.Keychecker<Char>,
                             stack: List<TreeNodeVisitor.StackContext<LevNode, Unit>>,
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
            return if(worstCase >= 0 && worstCase <= currentRow.minOrNull()!!) {
                TreeNodeVisitor.VisitOutcome.SKIP
            } else {
                TreeNodeVisitor.VisitOutcome.CONTINUE
            }
        }
    }

    object DamerauLevenshteinDistanceCalculator : DistanceCalculator() {
        override fun compute(keyChecker : Trie.Keychecker<Char>,
                             stack: List<TreeNodeVisitor.StackContext<LevNode, Unit>>,
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
            return if(worstCase >= 0 && worstCase <= prow.minOrNull()!!) {
                TreeNodeVisitor.VisitOutcome.SKIP
            } else {
                TreeNodeVisitor.VisitOutcome.CONTINUE
            }
        }
    }
}


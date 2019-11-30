package net.woggioni.klevtree

import net.woggioni.jwo.tree.StackContext
import net.woggioni.jwo.tree.TreeNodeVisitor
import net.woggioni.jwo.tree.TreeWalker
import net.woggioni.klevtree.node.TrieNode

interface Trie<T : TrieNode<KEY, PAYLOAD>, KEY, PAYLOAD> {

    interface Keychecker<KEY> {
        fun check(key1 : KEY?, key2 : KEY?) : Boolean
    }

    var keyChecker : Keychecker<KEY>

    val root : TrieNode<KEY, PAYLOAD>

    val tails : MutableList<TrieNode<KEY, PAYLOAD>>
    val words : Iterable<List<KEY>>
        get() {
            val res = object : Iterator<List<KEY>> {
                val it = tails.iterator()
                override fun hasNext(): Boolean {
                    return it.hasNext()
                }

                override fun next(): List<KEY> {
                    return it.next().linealDescendant()
                }
            }
            return object : Iterable<List<KEY>> {
                override fun iterator() : Iterator<List<KEY>> {
                    return res
                }
            }
        }

    private fun addNode(key : KEY?, parent : TrieNode<KEY, PAYLOAD>, prev : TrieNode<KEY, PAYLOAD>? = null) : TrieNode<KEY, PAYLOAD> {
        val result = TrieNode<KEY, PAYLOAD>(key)
        result.parent = parent
        if(prev != null) {
            prev.next = result
            result.prev = prev
        } else {
            when(parent.child) {
                null -> parent.child = result
                else -> {
                    var node : TrieNode<KEY, PAYLOAD>? = parent.child
                    while(node!!.next != null) {
                        node = node.next
                    }
                    node.next = result
                    result.prev = node
                }
            }
        }
        return result
    }

    fun add(path : Iterable<KEY>) : Pair<Boolean, TrieNode<KEY, PAYLOAD>?> {
        var result = false
        var pnode : TrieNode<KEY, PAYLOAD> = root
        var length = 0
        wordLoop@
        for(key in path) {
            ++length
            var cnode = pnode.child
            if(cnode != null) {
                while (true) {
                    if (cnode!!.key == key) {
                        pnode = cnode
                        continue@wordLoop
                    } else if (cnode.next == null) break
                    else cnode = cnode.next
                }
            }
            pnode = addNode(key, pnode, cnode)
            result = true
        }
        return if(result) {
            val tail = addNode(null, pnode)
            tails.add(tail)
            var node : TrieNode<KEY, PAYLOAD>? = tail
            while(node != null) {
                ++node.refCount
                node = node.parent
            }
            Pair(true, tail)
        } else {
            Pair(false, pnode)
        }
    }

    fun remove(path : List<KEY>) : Boolean {
        val deleteNode = { n : TrieNode<KEY, PAYLOAD> ->
            val parent = n.parent
            if(parent != null && parent.child == n) {
                parent.child = n.next
            }
            val prev = n.prev
            if(prev != null) {
                prev.next = n.next
            }
            val next = n.next
            if(next != null) {
                next.prev = n.prev
            }
            n.parent = null
        }
        return when(val res = search(path)) {
            null -> false
            else -> {
                var current = res
                do {
                    val parent = current!!.parent
                    if(--current.refCount == 0){
                        deleteNode(current)
                    }
                    current = parent
                } while(current != null)
                true
            }
        }
    }

    fun search(path : List<KEY>) : TrieNode<KEY, PAYLOAD>? {
        var result : TrieNode<KEY, PAYLOAD>? = null
        val visitor = object: TreeNodeVisitor<TrieNode<KEY, PAYLOAD>, Unit> {
            override fun visitPre(stack: List<StackContext<TrieNode<KEY, PAYLOAD>, Unit>>): TreeNodeVisitor.VisitOutcome {
                return if(stack.size == 1) {
                    TreeNodeVisitor.VisitOutcome.CONTINUE
                } else {
                    val lastNode = stack.last().node
                    val index = stack.size - 2
                    if (index < path.size) {
                        if(lastNode.key == path[index]) {
                            TreeNodeVisitor.VisitOutcome.CONTINUE
                        } else {
                            TreeNodeVisitor.VisitOutcome.SKIP
                        }
                    } else {
                        if (lastNode.key == null) {
                            result = lastNode
                        }
                        TreeNodeVisitor.VisitOutcome.EARLY_EXIT
                    }
                }
            }
        }
        val walker = TreeWalker<TrieNode<KEY, PAYLOAD>, Unit>(visitor)
        walker.walk(root)
        return result
    }
}
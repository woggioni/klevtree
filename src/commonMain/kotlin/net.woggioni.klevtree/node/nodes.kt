package net.woggioni.klevtree.node

import net.woggioni.klevtree.tree.TreeNodeVisitor


open class TrieNode<T, PAYLOAD>(val key : T?) : TreeNodeVisitor.TreeNode<TrieNode<T, PAYLOAD>> {
    var parent : TrieNode<T, PAYLOAD>? = null
    var child : TrieNode<T, PAYLOAD>? = null
    var next : TrieNode<T, PAYLOAD>? = null
    var prev : TrieNode<T, PAYLOAD>? = null
    var payload : PAYLOAD? = null
    var refCount = 0

    override fun children(): Iterator<TrieNode<T, PAYLOAD>> {
        return object : Iterator<TrieNode<T, PAYLOAD>> {
            var nextChild : TrieNode<T, PAYLOAD>? = child

            override fun hasNext(): Boolean = nextChild != null

            override fun next(): TrieNode<T, PAYLOAD> {
                val result = nextChild
                nextChild = nextChild?.next
                return result!!
            }
        }
    }

    fun linealDescendant() : List<T> {
        var node : TrieNode<T, PAYLOAD>? = this
        val chars = mutableListOf<T>()
        while(node != null) {
            val key = node.key
            if(key != null) {
                chars.add(key)
            }
            node = node.parent
        }
        return chars.asReversed()
    }
}

class CharNode<PAYLOAD>(key : Char?) : TrieNode<Char, PAYLOAD>(key)

class StringNode<PAYLOAD>(key : String?) : TrieNode<String, PAYLOAD>(key)

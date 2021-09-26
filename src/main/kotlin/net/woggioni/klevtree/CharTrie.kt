package net.woggioni.klevtree

import net.woggioni.klevtree.node.CharNode
import net.woggioni.klevtree.node.TrieNode

open class CharTrie<PAYLOAD> : Trie<CharNode<PAYLOAD>, Char, PAYLOAD>() {
    private class CaseInsensitiveKeyChecker : Keychecker<Char> {
        override fun check(key1: Char?, key2: Char?) = key1 == key2
    }

    private class CaseSensitiveKeyChecker : Keychecker<Char> {
        override fun check(key1: Char?, key2: Char?) = key1?.lowercaseChar() == key2?.lowercaseChar()
    }

    override val root: TrieNode<Char, PAYLOAD> = CharNode(null)
    override val tails = mutableListOf<TrieNode<Char, PAYLOAD>>()
    override var keyChecker: Keychecker<Char> = CaseSensitiveKeyChecker()

    var caseSensitive : Boolean = true
    set(value) {
        if(value) {
            keyChecker = CaseSensitiveKeyChecker()
        } else {
            keyChecker = CaseInsensitiveKeyChecker()
        }
        field = value
    }

    fun add(word : String) = super.add(word.asIterable())

    fun search(word : String) : TrieNode<Char, PAYLOAD>? = search(word.asIterable().toList())

    fun remove(word : String) = remove(word.asIterable().toList())
}
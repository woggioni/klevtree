package net.woggioni.klevtree

import net.woggioni.klevtree.node.CharNode
import net.woggioni.klevtree.node.TrieNode

interface ICharTrie<PAYLOAD> : Trie<CharNode<PAYLOAD>, Char, PAYLOAD> {

    class CaseInsensitiveKeyChecker : Trie.Keychecker<Char> {
        override fun check(key1: Char?, key2: Char?) = key1 == key2
    }

    class CaseSensitiveKeyChecker : Trie.Keychecker<Char> {
        override fun check(key1: Char?, key2: Char?) = key1?.toLowerCase() == key2?.toLowerCase()
    }

    var caseSensitive : Boolean

    fun add(word : String) = super.add(word.asIterable())

    fun search(word : String) : TrieNode<Char, PAYLOAD>? = search(word.asIterable().toList())

    fun remove(word : String) = remove(word.asIterable().toList())
}

class CharTrie<PAYLOAD> : ICharTrie<PAYLOAD> {

    override val root: TrieNode<Char, PAYLOAD> = CharNode(null)
    override val tails = mutableListOf<TrieNode<Char, PAYLOAD>>()
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
}
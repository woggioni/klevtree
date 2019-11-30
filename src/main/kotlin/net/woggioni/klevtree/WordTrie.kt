package net.woggioni.klevtree

import net.woggioni.klevtree.node.StringNode
import net.woggioni.klevtree.node.TrieNode

interface IWordTrie<PAYLOAD> : Trie<StringNode<PAYLOAD>, String, PAYLOAD> {

    class CaseInsensitiveKeyChecker : Trie.Keychecker<String> {
        override fun check(key1: String?, key2: String?) = key1 == key2
    }

    class CaseSensitiveKeyChecker : Trie.Keychecker<String> {
        override fun check(key1: String?, key2: String?) = key1?.toLowerCase() == key2?.toLowerCase()
    }

    var caseSensitive : Boolean

    fun add(word : String, delimiter : String) = super.add(word.split(delimiter))

    fun search(word : String, delimiter : String) : TrieNode<String, PAYLOAD>? {
        return search(word.split(delimiter))
    }
}

class WordTrie<PAYLOAD> : IWordTrie<PAYLOAD> {

    override val root: TrieNode<String, PAYLOAD> = StringNode(null)
    override val tails = mutableListOf<TrieNode<String, PAYLOAD>>()
    override var keyChecker: Trie.Keychecker<String> = IWordTrie.CaseSensitiveKeyChecker()

    override var caseSensitive : Boolean = true
        set(value) {
            if(value) {
                keyChecker = IWordTrie.CaseSensitiveKeyChecker()
            } else {
                keyChecker = IWordTrie.CaseInsensitiveKeyChecker()
            }
            field = value
        }
}
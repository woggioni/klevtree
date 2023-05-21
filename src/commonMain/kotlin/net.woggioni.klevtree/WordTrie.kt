package net.woggioni.klevtree

import net.woggioni.klevtree.node.StringNode
import net.woggioni.klevtree.node.TrieNode

open class WordTrie<PAYLOAD> : Trie<StringNode<PAYLOAD>, String, PAYLOAD>() {

    override val root: TrieNode<String, PAYLOAD> = StringNode(null)
    override val tails = mutableListOf<TrieNode<String, PAYLOAD>>()
    override var keyChecker: Keychecker<String> = CaseSensitiveKeyChecker()

    private class CaseInsensitiveKeyChecker : Keychecker<String> {
        override fun check(key1: String?, key2: String?) = key1 == key2
    }

    private class CaseSensitiveKeyChecker : Keychecker<String> {
        override fun check(key1: String?, key2: String?) = key1?.lowercase() == key2?.lowercase()
    }

    var caseSensitive : Boolean = true
        set(value) {
            if(value) {
                keyChecker = CaseSensitiveKeyChecker()
            } else {
                keyChecker = CaseInsensitiveKeyChecker()
            }
            field = value
        }

    fun add(word : String, delimiter : String) = super.add(word.split(delimiter))

    fun search(word : String, delimiter : String) : TrieNode<String, PAYLOAD>? {
        return search(word.split(delimiter))
    }
}
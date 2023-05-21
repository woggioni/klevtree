package net.woggioni.klevtree.tree

/**
 * This interface must be implemented by the user of [TreeWalker] and its methods will be called by
 * [TreeWalker.walk]. The methods will receive as an input a list of [StackContext]
 * instances each one correspond to a node in the tree, each node is preceded in the list
 * by its parents in the tree. Each instance has a method, [StackContext.context]
 * to set a custom object that can be used in the [.visitPre] method and the method
 * [StackContext.context] that can be used in the [.visitPost] method to retrieve
 * the same instance. This is to provide support for algorithms that require both pre-order and post-order logic.
 * The last element of the list corresponds to the node currently being traversed.
 * @param <T> the type of the context object used
</T> */
interface TreeNodeVisitor<NODE : TreeNodeVisitor.TreeNode<NODE>, T> {
    interface TreeNode<NODE : TreeNode<NODE>> {
        fun children(): Iterator<NODE>?
    }

    /**
     * This interface exposes the methods that are visible to the user of
     * [TreeWalker], it allows to
     * set/get a custom object in the current stack context or to get the current link's Aci
     * @param <T> the type of the context object used
    </T> */
    interface StackContext<NODE : TreeNode<*>?, T> {
        /**
         * @return the current user object
         */
        /**
         * @param ctx the user object to set for this stack level
         */
        var context: T?

        /**
         * @return the current TreeNode
         */
        val node: NODE
    }

    enum class VisitOutcome {
        CONTINUE,
        SKIP,
        EARLY_EXIT
    }

    /**
     * This method will be called for each link using
     * [a Depth-first pre-oder algorithm](https://en.wikipedia.org/wiki/Tree_traversal#Pre-order_(NLR))
     * @param stack is a list of [StackContext] instances corresponding to the full path from the root to the
     * current node in the tree
     * @return a boolean that will be used to decide whether to traverse the subtree rooted in the current link or not
     */
    fun visitPre(stack: List<StackContext<NODE, T>>): VisitOutcome {
        return VisitOutcome.CONTINUE
    }

    /**
     * This method will be called for each node using
     * [a Depth-first post-oder algorithm](https://en.wikipedia.org/wiki/Tree_traversal#Post-order_(LRN))
     * @param stack is a list of [StackContext] instances corresponding to the full path from the root to the
     * current node in the tree
     */
    fun visitPost(stack: List<StackContext<NODE, T>>) {}
}

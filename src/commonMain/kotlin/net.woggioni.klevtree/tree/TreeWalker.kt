package net.woggioni.klevtree.tree


class TreeWalker<NODE : TreeNodeVisitor.TreeNode<NODE>, T>(
    private val visitor: TreeNodeVisitor<NODE, T>
) {

    private class StackElement<NODE : TreeNodeVisitor.TreeNode<NODE>, T>(override val node: NODE) :
        TreeNodeVisitor.StackContext<NODE, T> {
        override var context: T? = null
        var childrenIterator: Iterator<NODE>? = null
    }

    /**
     * This methods does the actual job of traversing the tree calling the methods of the provided
     * [TreeNodeVisitor] instance
     * @param root the root node of the tree
     */
    fun walk(root: NODE) {
        val stack: MutableList<StackElement<NODE, T>> = mutableListOf()
        val rootStackElement = StackElement<NODE, T>(root)
        stack.add(rootStackElement)
        val publicStack: List<TreeNodeVisitor.StackContext<NODE, T>> = stack
        when (visitor.visitPre(publicStack)) {
            TreeNodeVisitor.VisitOutcome.CONTINUE -> rootStackElement.childrenIterator = root.children()
            TreeNodeVisitor.VisitOutcome.SKIP -> rootStackElement.childrenIterator = null
            TreeNodeVisitor.VisitOutcome.EARLY_EXIT -> return
        }
        while (stack.isNotEmpty()) {
            val lastElement: StackElement<NODE, T> = stack.last()
            val childrenIterator = lastElement.childrenIterator
            if (childrenIterator != null && childrenIterator.hasNext()) {
                val childNode = childrenIterator.next()
                val childStackElement = StackElement<NODE, T>(childNode)
                stack.add(childStackElement)
                when (visitor.visitPre(publicStack)) {
                    TreeNodeVisitor.VisitOutcome.CONTINUE -> childStackElement.childrenIterator = childNode.children()
                    TreeNodeVisitor.VisitOutcome.SKIP -> childStackElement.childrenIterator = null
                    TreeNodeVisitor.VisitOutcome.EARLY_EXIT -> return
                }
            } else {
                visitor.visitPost(publicStack)
                stack.removeLast()
            }
        }
    }
}


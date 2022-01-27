import java.util.function.Consumer;

class AVLTree {
    private AVLTreeNode root;

    public AVLTree() { // Initialise the root
        this.root = null;
    }

    public AVLTreeNode getRoot() {
        return this.root;
    }

    private void setRoot(AVLTreeNode root) {
        this.root = root;
    }

    public void createTestTree() { // Test tree containing a tree created for testing purposes.
        var tree4 = new AVLTreeNode("4", null);
        var tree2 = new AVLTreeNode("2", tree4);
        var tree1 = new AVLTreeNode("1", tree2);
        var tree3 = new AVLTreeNode("3", tree2);
        var tree6 = new AVLTreeNode("6", tree4);
        var tree5 = new AVLTreeNode("5", tree6);
        var tree7 = new AVLTreeNode("7", tree6);
        tree1.setHeight(0);
        tree2.setHeight(1);
        tree3.setHeight(0);
        tree4.setHeight(2);
        tree5.setHeight(0);
        tree6.setHeight(1);
        tree7.setHeight(0);
        tree4.setRight(tree6);
        tree4.setLeft(tree2);
        tree2.setLeft(tree1);
        tree2.setRight(tree3);
        tree6.setLeft(tree5);
        tree6.setRight(tree7);
        setRoot(tree4);
    }

    public void print() {
        printPreOrder(getRoot(), "");
    }

    private void printPreOrder(AVLTreeNode current, String indentation) { // Prints the tree, each space = +1 height
        if (current == null) {
            return;
        }
        System.out.println(indentation + current.getElement());
        printPreOrder(current.getLeft(), indentation + " ");
        printPreOrder(current.getRight(), indentation + " ");
    }

    public boolean inTree(String e) { // Returns if an item exists in the tree.
        AVLTreeNode item = searchTree(e, getRoot());
        return item != null;
    }

    private AVLTreeNode searchTree(String e, AVLTreeNode current) { // Searches the tree
        int result;
        while (current != null) { // Traverse the entire tree
            result = current.compareStringToThis(e); // Uses internal comparing method.
            // Go down the correct branches
            if (result < 0) {
                current = current.getLeft();
            } else if (result > 0) {
                current = current.getRight();
            } else {
                return current;
            }
        }
        return null;
    }

    public void insert(String e) { // Insert a node into the tree
        AVLTreeNode current = getRoot();
        if (current == null) {
            setRoot(new AVLTreeNode(e, null));
            getRoot().setHeight(0);
            return;
        }
        insertIntoTree(e, current);
    }

    private void insertIntoTree(String e, AVLTreeNode current) { // Insert a node into the tree
        AVLTreeNode previous = current;
        int result = current.getElement().compareTo(e);
        // Go down the correct branches to insert the node into an appropriate spot.
        if (result < 0) {
            current = current.getRight();
            if (current == null) {
                previous.setRight(new AVLTreeNode(e, previous));
                previous.getRight().setHeight(0);
                updateHeights();
                balanceTree();
                return;
            }
            insertIntoTree(e, current);
        } else if (result > 0) {
            current = current.getLeft();
            if (current == null) {
                previous.setLeft(new AVLTreeNode(e, previous));
                previous.getLeft().setHeight(0);
                updateHeights();
                balanceTree();
                return;
            }
            insertIntoTree(e, current);
        }
    }

    public void remove(String e) { // Remove a node from the tree
        AVLTreeNode root = getRoot();
        AVLTreeNode node = searchTree(e, root);
        if (node == null) {
            System.out.println("Node \"" + e + "\" does not exist.");
            return;
        }
        AVLTreeNode parent = node.getParent();
        AVLTreeNode leftSibling = node.getLeft();
        AVLTreeNode rightSibling = node.getRight();
        AVLTreeNode replacementNode;
        boolean isRoot = node == root;
        boolean isLeftChild = false; // Initialise as false
        if (parent != null) {
            isLeftChild = parent.getLeft() == node;
        }
        boolean hasLeftChild = leftSibling != null;
        boolean hasRightChild = rightSibling != null;
        boolean hasOneChild = hasLeftChild || hasRightChild;
        boolean hasTwoChildren = hasLeftChild && hasRightChild;
        if (!hasOneChild) { // Has no children
            if (isRoot) {
                setRoot(null);
            } else {
                if (isLeftChild) parent.setLeft(null);
                else parent.setRight(null); // parent can't be null as isRoot is not triggered.
            }
        } else if (hasTwoChildren) {
            replacementNode = assignReplacementNodeL(node);
            if (isLeftChild) parent.setLeft(replacementNode);
            else parent.setRight(replacementNode); // parent can't be null as isRoot is not triggered.
        } else { // One child
            replacementNode = hasLeftChild ? leftSibling : rightSibling;
            if (isRoot) {
                setRoot(replacementNode);
                replacementNode.setParent(null);
            } else {
                if (isLeftChild) parent.setLeft(replacementNode); // Parent's left child
                else
                    parent.setRight(replacementNode); // Parent's right child - parent can't be null as isRoot is not triggered.
                replacementNode.setParent(parent);
            }
        }
        updateHeights();
        balanceTree();
        System.out.println(node.getElement() + " was removed.");
    }

    private AVLTreeNode assignReplacementNodeL(AVLTreeNode toRemove) { // Assign rightmost node in the left subtree
        if (toRemove == null) return null;
        AVLTreeNode leftChild = toRemove.getLeft();
        AVLTreeNode rightChild = toRemove.getRight();
        if (leftChild == null && rightChild == null) return null;
        else if (leftChild == null) { // If there is no left subtree, execute another method that assigns the leftmost node in the right subtree.
            return assignReplacementNodeR(toRemove);
        }
        boolean traversed = false;
        AVLTreeNode replacementNode = leftChild;
        AVLTreeNode parent = toRemove;
        while (replacementNode.getRight() != null) {
            traversed = true;
            parent = replacementNode;
            replacementNode = replacementNode.getRight();
        }
        if (traversed) {
            parent.setRight(replacementNode.getLeft());
            parent.getRight().setParent(parent);
            replacementNode.setLeft(leftChild);
            replacementNode.getLeft().setParent(replacementNode);
        } else {
            replacementNode.setLeft(null);
        }
        replacementNode.setRight(rightChild);
        if (rightChild != null) {
            rightChild.setParent(replacementNode);
        }
        if (toRemove == getRoot()) setRoot(replacementNode);
        replacementNode.setParent(toRemove.getParent());
        return replacementNode;
    }

    private AVLTreeNode assignReplacementNodeR(AVLTreeNode toRemove) { // Assign the leftmost node in the right subtree
        if (toRemove == null) return null;
        AVLTreeNode rightChild = toRemove.getRight();
        if (rightChild == null) return null;
        boolean traversed = false;
        AVLTreeNode replacementNode = rightChild;
        AVLTreeNode parent = toRemove;
        while (replacementNode.getLeft() != null) {
            traversed = true;
            parent = replacementNode;
            replacementNode = replacementNode.getLeft();
        }
        if (traversed) {
            parent.setLeft(replacementNode.getRight());
            parent.getLeft().setParent(parent);
            replacementNode.setRight(rightChild);
            replacementNode.getRight().setParent(replacementNode);
        } else {
            replacementNode.setRight(null);
        }
        replacementNode.setLeft(toRemove.getLeft()); // => null. This method is only called when toRemove.left == null.
        if (toRemove == getRoot()) setRoot(replacementNode);
        replacementNode.setParent(toRemove.getParent());
        return replacementNode;
    }

    private void postOrderTraversal(AVLTreeNode current, Consumer<AVLTreeNode> block) { // Post-order traversal
        if (current == null) {
            return;
        }
        postOrderTraversal(current.getLeft(), block);
        postOrderTraversal(current.getRight(), block);
        block.accept(current); // Run block
    }

    private void updateHeights() { // Update heights
        postOrderTraversal(getRoot(), this::updateHeights);
    }

    private void updateHeights(AVLTreeNode node) { // Update height of passed node
        if (node.getLeft() != null || node.getRight() != null) {
            node.setHeight(node.getChildHeight() + 1);
        } else {
            node.setHeight(0);
        }
    }

    private void balanceTree() { // Balance tree
        postOrderTraversal(getRoot(), this::rebalanceTree);
    }

    private void rebalanceTree(AVLTreeNode current) { // Rebalance the tree
        int balance, leftHeight = 0, rightHeight = 0;
        AVLTreeNode leftChild = current.getLeft();
        AVLTreeNode rightChild = current.getRight();
        if (leftChild != null) {
            leftHeight = current.getLeftHeight() + 1;
        }
        if (rightChild != null) {
            rightHeight = current.getRightHeight() + 1;
        }
        balance = rightHeight - leftHeight;
        current.setBalance(balance);

        if (balance < -1) {
            if (leftChild == null) return;
            leftHeight = leftChild.getLeft() != null ? leftChild.getLeftHeight() + 1 : 0;
            rightHeight = leftChild.getRight() != null ? leftChild.getRightHeight() + 1 : 0;
            if (leftHeight >= rightHeight) {
                rotateLL(current);
            } else {
                rotateLR(current);
            }
            updateHeights();
        } else if (balance > 1) {
            if (rightChild == null) return;
            leftHeight = rightChild.getLeft() != null ? rightChild.getLeftHeight() + 1 : 0;
            rightHeight = rightChild.getRight() != null ? rightChild.getRightHeight() + 1 : 0;
            if (rightHeight >= leftHeight) {
                rotateRR(current);
            } else {
                rotateRL(current);
            }
            updateHeights();
        }
    }

    private void rotateLL(AVLTreeNode current) { // Right-right rotation about the node (weight on left subtree of left subtree)
        var left = current.getLeft();
        var leftRight = left.getRight();
        left.setRight(current);
        current.setLeft(leftRight);
        if (getRoot() == current) {
            setRoot(left);
        } else {
            current.getParent().setLeft(left);
        }
        if (leftRight != null) leftRight.setParent(current);
        left.setParent(current.getParent());
        current.setParent(left);
    }

    private void rotateLR(AVLTreeNode current) { // Left-right rotation about the node (weight on right subtree of left subtree)
        AVLTreeNode leftChild = current.getLeft();
        current.setLeft(rotateR(leftChild));
        leftChild.setParent(current);
        updateHeights();
        rotateLL(current);
    }

    private AVLTreeNode rotateL(AVLTreeNode current) { // Left rotation
        var left = current.getLeft();
        var leftRight = left.getRight();
        left.setRight(current);
        current.setLeft(leftRight);
        current.setParent(left);
        if (leftRight != null) leftRight.setParent(current);
        return left;
    }

    private void rotateRR(AVLTreeNode current) { // Left-left rotation about the node (weight on right subtree of right subtree)
        var right = current.getRight();
        var rightLeft = right.getLeft();
        right.setLeft(current);
        current.setRight(rightLeft);
        if (getRoot() == current) {
            setRoot(right);
        } else {
            current.getParent().setRight(right);
        }
        if (rightLeft != null) rightLeft.setParent(current);
        right.setParent(current.getParent());
        current.setParent(right);
    }

    private void rotateRL(AVLTreeNode current) { // Right-left rotation about the node (weight on left subtree of right subtree)
        AVLTreeNode rightChild = current.getRight();
        current.setRight(rotateL(rightChild));
        rightChild.setParent(current);
        updateHeights();
        rotateRR(current);
    }

    private AVLTreeNode rotateR(AVLTreeNode current) { // Right rotation
        var right = current.getRight();
        var rightLeft = right.getLeft();
        right.setLeft(current);
        current.setRight(rightLeft);
        current.setParent(right);
        if (rightLeft != null) rightLeft.setParent(current);
        return right;
    }

    public static void main(String[] args) {

    }
}

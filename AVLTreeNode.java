class AVLTreeNode {
    private String element;

    private int height;
    private int balance;

    private AVLTreeNode left = null;
    private AVLTreeNode right = null;
    private AVLTreeNode parent;

    AVLTreeNode(String e, AVLTreeNode parent) {
        element = e;
        this.parent = parent;
        height = 1;
    }

    public String getElement() {
        return this.element;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public AVLTreeNode getLeft() {
        return this.left;
    }

    public void setLeft(AVLTreeNode left) {
        this.left = left;
    }

    public AVLTreeNode getRight() {
        return this.right;
    }

    public void setRight(AVLTreeNode right) {
        this.right = right;
    }

    public AVLTreeNode getParent() {
        return this.parent;
    }

    public void setParent(AVLTreeNode parent) {
        this.parent = parent;
    }

    public int getRightHeight() {
        if (right == null) {
            return 0;
        } else {
            return right.height;
        }
    }

    public int getLeftHeight() {
        if (left == null) {
            return 0;
        } else {
            return left.height;
        }
    }

    public int getChildHeight() {
        return Math.max(getRightHeight(), getLeftHeight());
    }

    public int compareStringToThis(String s) {
//        System.out.println("Comparing " + s + " with " + element);
        return s.compareTo(element);
    }

    public String toString() {
      return element;
    }
}

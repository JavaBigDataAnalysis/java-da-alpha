import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SizeBalancedTree <T extends Comparable<T>> {
    public class Node {
        //data域：存放数据项
        T data;
        //size域：存放树的大小（节点数目）
        int size;
        Node parent;
        Node left;
        Node right;

        public Node(T data, int size, Node parent, Node left, Node right) {
            this.data = data;
            this.size = size;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return "[data=" + data + ",size=" + size + "]";
        }
    }

    //根节点
    private Node root;

    public Node root() {
        return root;
    }

    public SizeBalancedTree() {
        root = null;
    }

    public SizeBalancedTree(T data) {
        root = new Node(data, 1, null, null, null);
    }

    /**
     * 往根为root的树中插入data
     *
     * @param data
     * @param root
     **/
    public void insert(T data) {

        if (root == null) { //如果根为空
            root = new Node(data, 1, null, null, null);
        } else {
            Node current = root;
            Node parent = null;
            int result = 0;
            while (current != null) {
                parent = current;
                result = data.compareTo(current.data);
                if (result > 0) {
                    current = current.right;
                } else {
                    current = current.left;
                }
            }
            Node newNode = new Node(data, 1, parent, null, null);
            if (result > 0) {
                parent.right = newNode;
            } else {
                parent.left = newNode;
            }

            ancestorAdd1(newNode);

            maintain(newNode);
        }
    }

    private void ancestorAdd1(Node node) {
        Node current = node;
        while (current.parent != null) {
            current.parent.size += 1;
            current = current.parent;
        }
    }

    private void ancestorminu1(Node node) {
        Node current = node;
        while (current.parent != null) {
            current.parent.size -= 1;
            current = current.parent;
        }
    }

    private boolean trueOrFalse(Node node) {
        Node current = node;
        while (current != null) {
            if (current == root.left) {
                return false;
            } else if (current == root.right) {
                return true;
            }
            current = current.parent;
        }

        return false;
    }

    private void maintain(Node node) { //node为新添加的节点
        if (root == node.parent) {
            return;
        } else {
            maintainHelp(root, trueOrFalse(node));
        }

    }

    private void maintainHelp(Node node, boolean flag) { //传过来的node为新添加的节点
        if (node != null) {
			/*Node t = node;
			Node l = t.left;
			Node r = t.right;*/
            if (!flag && node.left != null) { //左边
                if ((node.right == null && node.left.left != null) || (node.left.left != null && node.left.left.size > node.right.size)) { //case1
                    right_rot(node);
                } else if ((node.right == null && node.left.right != null) || (node.left.right != null && node.left.right.size > node.right.size)) {//case2
                    left_rot(node.left);
                    right_rot(node);
                } else {
                    return;
                }
                maintainHelp(node.left, false);
                maintainHelp(node.right, true);
                maintainHelp(node, false);
                maintainHelp(node, true);
            } else if (flag && node.right != null) { //右边
                if ((node.left == null && node.right.left != null) || (node.right.left != null && node.right.left.size > node.left.size)) { //case2*
                    right_rot(node.right);
                    left_rot(node);
                } else if ((node.left == null && node.right.right != null) || (node.right.right != null && node.right.right.size > node.left.size)) {//case1*
                    left_rot(node);
                } else {
                    return;
                }
                maintainHelp(node.left, false);
                maintainHelp(node.right, true);
                maintainHelp(node, false);
                maintainHelp(node, true);
            }
        }
    }

    /**
     * 从根为node的树中删除数据元素为data的节点
     *
     * @param node
     * @param data
     */
    public void remove(Node node, T data) {
        Node del = find(data);
        if (del != null) {
            boolean what = trueOrFalse(del);
            if (del.left == null && del.right == null) {
                if (del == root) {
                    root = null;
                } else {
                    ancestorminu1(del);
                    if (del == del.parent.left) {
                        del.parent.left = null;
                    } else {
                        del.parent.right = null;
                    }
                    del.parent = null;
                    maintainHelp(root, what);
                }
            } else if (del.right != null && del.left == null) {
                //因为已经平衡，所以要删除的节点del有且仅有一个右子节点
                if (root == del) {
                    root = del.right;
                    del.right.parent = null;
                    del.right = null;
                } else {
                    ancestorminu1(del);
                    if (del == del.parent.left) {
                        del.parent.left = del.right;
                    } else {
                        del.parent.right = del.right;
                    }
                    del.right.parent = del.parent;
                    del.parent = del.right = null;
                    maintainHelp(root, what);
                }
            } else if (del.left != null && del.right == null) {
                //因为已经平衡，所以要删除的节点del有且仅有一个左子节点
                if (root == del) {
                    root = del.left;
                    del.left.parent = null;
                    del.left = null;
                } else {
                    ancestorminu1(del);
                    if (del == del.parent.left) {
                        del.parent.left = del.left;
                    } else {
                        del.parent.right = del.left;
                    }
                    del.left.parent = del.parent;
                    del.parent = del.left = null;
                    maintainHelp(root, what);
                }
            } else { //左右子树都不为空
                Node preOfDel = pre(del, data);
                del.data = preOfDel.data;
                ancestorminu1(preOfDel);
                preOfDel.parent.left = preOfDel.left;
                if (preOfDel.left != null) {
                    preOfDel.left.parent = preOfDel.parent;
                }
                preOfDel.parent = preOfDel.left = null;
                maintainHelp(root, what);
            }

        } else {
            return;
        }

    }

    /**
     * 在树中查找键值为data的结点
     *
     * @param data
     * @return
     */
    public Node find(T data) {
        Node current = root;
        if (root == null) {
            return null;
        } else {
            int result;
            while (current != null) {
                result = data.compareTo(current.data);
                if (result > 0) {
                    current = current.right;
                } else if (result < 0) {
                    current = current.left;
                } else {
                    return current;
                }
            }
        }
        return null;
    }

    /**
     * MAC地址的查询与替换
     *
     * @param data
     */
    public void findAndChange(T data) {
        Node current = root;
        if (root == null) {
            System.out.println("Changed Error: Root");
        } else {
            int result;
            while (current != null) {
                result = data.compareTo(current.data);
                if (result > 0) {
                    current = current.right;
                } else if (result < 0) {
                    current = current.left;
                } else {
                    current.data = data;
                    break;
                }
            }
        }
    }

    /**
     * 返回IP树中对应IP范围代表的省份
     *
     * @param data
     * @return
     */
    public String findIp(T data) {
        Node current = root;
        if (root == null) {
            return null;
        } else {
            int result;
            while (current != null) {
                result = data.compareTo(current.data);

                if (result > 0 && current.right != null) {
                    current = current.right;
                } else if (result > 0 && current.right == null) {
                    return current.data.toString();
                } else if (result < 0 && current.left != null) {
                    current = current.left;
                } else if (result < 0 && current.left == null) {
                    return current.data.toString();
                } else if (result == 0) {
                    return current.parent.data.toString();
                }
            }
        }
        return current.parent.data.toString();
    }

    /**
     * 在树中查找排名为k的节点
     *
     * @param node
     * @param k
     * @return
     */
    public Node select(Node node, int k) {

        if (root == null) {
            try {
                throw new Exception("该树为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (root.size < k) {
            try {
                throw new Exception("该节点不存在一个排名为" + k + "的节点，但存在一个最大排名为" + root.size + "的节点");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (node != null) {
                int result;
                if (node.left != null) {
                    result = node.left.size + 1;
                    if (result == k) {
                        return node;
                    } else if (result < k) {
                        return select(node.right, k - result);
                    } else {
                        return select(node.left, k);
                    }
                } else if (node.right != null) {
                    if (k == 1) {
                        return node;
                    } else {
                        return select(node.right, k - 1);
                    }
                } else {
                    return node;
                }
            }
        }
        return null;
    }

    //最大值
    public T minData() {
        return select(root, 1).data;
    }

    //最小值
    public T maxData() {
        return select(root, this.root().size).data;
    }

    //这里考虑到了要查看排名的数据元素不在树中
    public int rank(Node node, T data) {
        Node p = find(data);
        T m = minData();
        return data.compareTo(m) < 0 ? 1 : (p == null ? cRank(node, data) + 1 : cRank(node, data));
    }

    //返回以node为根的树中元素值为data的排名
    private int cRank(Node node, T data) {
        if (node != null) {
            int result = data.compareTo(node.data);
            if (node.left != null && node.right != null) {
                if (result < 0) {
                    return cRank(node.left, data);
                } else if (result > 0) {
                    return cRank(node.left, data) + cRank(node.right, data) + 1;
                }
                return node.left.size + 1;

            } else if (node.right != null && node.left == null) {
                if (result < 0) {
                    return 0;
                } else if (result > 0) {
                    return cRank(node.right, data) + 1;
                }
                return 1;
            } else if (node.left != null && node.right == null) {
                if (result < 0) {
                    return cRank(node.left, data);
                } else if (result > 0) {
                    return cRank(node.left, data) + 1;
                }

                return cRank(node.left, data) + 1;


            } else {
                if (result < 0) {
                    return 0;
                } else if (result > 0) {
                    return 1;
                }
                return 1;
            }
        }
        return 0;
    }

    //以node节点为根某数据元素的前驱
    public Node pre(Node node, T data) {
        Node current = node;
        Node preNode = null;
        int result;
        while (current != null) {
            result = data.compareTo(current.data);
			/*if(result == 0){
				return current;
			}else*/
            if (result > 0) {
                preNode = current;
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return preNode;
    }

    //以node节点为根某数据元素的后继
    public Node succ(Node node, T data) {
        Node current = node;
        Node preNode = null;
        int result;
        while (current != null) {
            result = data.compareTo(current.data);
			/*if(result == 0){
				return current;
			}else */
            if (result < 0) {
                preNode = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return preNode;
    }

    /**
     * 右旋(x/y才是关键)
     *
     * @param x        │			│
     *                 x             y
     *                ││     -      ││
     *             y──┘└─γ		 α──┘└─x
     *            ││				   ││
     *          α─┘└─β				 β─┘└─γ
     */
    private void right_rot(Node x) {
        Node y = x.left;
        y.parent = x.parent;
        if (x.parent != null) {
            if (x == x.parent.left) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
        }
        x.left = y.right;
        if (y.right != null) {
            y.right.parent = x;
        }
        y.right = x;
        x.parent = y;

        y.size = x.size;
        x.size = (y.left == null ? y.size - 0 - 1 : y.size - y.left.size - 1);

        if (root == x) {
            root = y;
        }
    }

    /**
     * 左旋(x/y才是关键)
     *
     * @param x │				│
     *          x				y
     *          ││     -> 		││
     *       α──┘└─y		 x──┘└─γ
     *            ││		││
     *          β─┘└─γ	  α─┘└─β
     */
    private void left_rot(Node x) {
        Node y = x.right;
        y.parent = x.parent;
        if (x.parent != null) {
            if (x == x.parent.left) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
        }
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }
        y.left = x;
        x.parent = y;

        y.size = x.size;
        x.size = (y.right == null ? y.size - 0 - 1 : y.size - y.right.size - 1);

        if (root == x) {
            root = y;
        }
    }

    //广度优先遍历
    public List<Node> breadthFirstSearch() {
        return cBreadthFirstSearch(root);
    }

    //TODO: 此处需要针对IP和MAC重写BFS,仅提取各自所需要的信息即可
    private List<Node> cBreadthFirstSearch(Node node) {
        List<Node> nodes = new ArrayList<Node>();
        Deque<Node> deque = new ArrayDeque<Node>();
        if (node != null) {
            deque.offer(node);
        }
        while (!deque.isEmpty()) {
            Node tmp = deque.poll();
            nodes.add(tmp);
            if (tmp.left != null) {
                deque.offer(tmp.left);
            }
            if (tmp.right != null) {
                deque.offer(tmp.right);
            }
        }
        return nodes;
    }
}

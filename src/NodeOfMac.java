public class NodeOfMac implements Comparable<NodeOfMac> {
    //MAC地址
    private long value;
    //计数器
    private int counter;

    public NodeOfMac() {
        this.value = 0;
        this.counter = 0;
    }

    public NodeOfMac(long value,int counter) {
        this.value = value;
        this.counter = counter;
    }

    /**
     * 设置节点的MAC地址
     *
     * @param value Long Integer
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * 设置MAC地址的计数器
     *
     * @param counter Integer
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * 返回节点的MAC地址
     *
     * @return
     */
    public long getValue() {
        return this.value;
    }

    /**
     * 返回节点对应的计数器
     *
     * @return
     */
    public String getCounter() {
        return String.valueOf(counter);
    }

    @Override
    public int compareTo(NodeOfMac o) {
        if (this.getValue() > o.getValue()) {
            return 1;
        } else if (this.getValue() < o.getValue()) {
            return -1;
        }
        else
            return 0;
    }

    @Override
    public String toString () {
        return this.getCounter();
    }
}

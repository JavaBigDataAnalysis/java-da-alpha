public class NodeOfIp implements Comparable<NodeOfIp> {
    //IP地址
    private int value;
    //区域
    private String Area;

    public NodeOfIp() {
        this.value = 0;
        this.Area = "";
    }

    public NodeOfIp(int value,String Area) {
        this.value = value;
        this.Area = Area;
    }

    /**
     * 设置节点的IP地址
     *
     * @param value Integer
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * 设置节点的所属区域
     *
     * @param area String
     */
    public void setArea(String area) {
        this.Area = Area;
    }

    /**
     * 返回节点IP地址
     *
     * @return value Integer
     */
    public int getValue() {
        return this.value;
    }

    /**
     * 返回节点的所属区域
     *
     * @return area String
     */
    public String getArea() {
        return this.Area;
    }

    @Override
    public int compareTo(NodeOfIp o) {
        if (this.getValue() > o.getValue()){
            return 1;
        }
        else
            if (this.getValue() < o.getValue()) {
                return -1;
            }
            else
                return 0;
    }

    @Override
    public String toString() {
        return this.getArea();
    }

}

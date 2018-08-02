public class MacAddressStatistics {

    private SizeBalancedTree macAddressTree;

    public MacAddressStatistics () {
        this.macAddressTree = new SizeBalancedTree<NodeOfMac>();
    }

    /**
     * 传入MAC地址，搜索MAC的SBT，新建节点或替换节点
     *
     * @param macAddress
     */
    public void insert(String macAddress) {
        NodeOfMac tempNodeMac = new NodeOfMac(MacUtil.macToLong(macAddress),1);
        SizeBalancedTree.Node tempNode = macAddressTree.find(tempNodeMac);
        if (tempNode == null){
            macAddressTree.insert(tempNodeMac);
        }
        else {

            tempNodeMac.setCounter(Integer.parseInt(tempNode.data.toString())+1);
            macAddressTree.findAndChange(tempNodeMac);
        }
    }

    /**
     * 遍历SBT，返回结果
     */
    public void getAnswer () {
        System.out.println(macAddressTree.breadthFirstSearch());
    }

}

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class MacAddressStatistics {

    private SizeBalancedTree macAddressTree;
    private Map<String,Integer> map;
    private Map<String,Integer> countOfMac;

    public MacAddressStatistics () {
        //this.map = Collections.synchronizedMap(new HashMap<String, Integer>());
        this.map = new ConcurrentHashMap<String, Integer>();
        ValueComparator bvc = new ValueComparator(map);
        this.countOfMac = Collections.synchronizedMap(new TreeMap<String, Integer>(bvc));
        //this.macAddressTree = new SizeBalancedTree<NodeOfMac>();
    }

    /**
     * 传入MAC地址，搜索MAC的SBT，新建节点或替换节点
     *
     * @param macAddress
     */
    public void insert(String macAddress) {
        if (map.containsKey(macAddress)) {
            Integer times = map.get(macAddress);
            map.put(macAddress, times+1);
        }
        else {
            map.put(macAddress, 1);
        }
//        NodeOfMac tempNodeMac = new NodeOfMac(MacUtil.macToLong(macAddress),1);
//        SizeBalancedTree.Node tempNode = macAddressTree.find(tempNodeMac);
//        if (tempNode == null){
//            macAddressTree.insert(tempNodeMac);
//        }
//        else {
//
//            tempNodeMac.setCounter(Integer.parseInt(tempNode.data.toString())+1);
//            macAddressTree.findAndChange(tempNodeMac);
//        }
    }

    /**
     * 遍历SBT，返回结果
     */
    public Map<String,Integer> getAnswer () {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
                        //entry.getKey() ;entry.getValue(); entry.setValue();
                        //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
//                         System.out.println("key= " + entry.getKey() + " and value= "
//                                + entry.getValue());
                         if (entry.getValue()>1000){
                             this.countOfMac.put(entry.getKey(), entry.getValue());
                         }
                     }
        //this.countOfMac.putAll(map);
        return this.countOfMac;
        //System.out.println(macAddressTree.breadthFirstSearch());
    }

}

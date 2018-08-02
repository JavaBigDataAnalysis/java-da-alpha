import java.io.*;

public class IpAreaRange {

    private String ipRangeFileName;
    private SizeBalancedTree ipRangeTree_S;
    private SizeBalancedTree ipRangeTree_T;

    public IpAreaRange() {
        this.ipRangeFileName = "" ;
        this.ipRangeTree_S = new SizeBalancedTree();
        this.ipRangeTree_T = new SizeBalancedTree();
    }

    public IpAreaRange(String fileName) {
        this.ipRangeFileName = fileName;
        this.ipRangeTree_S = new SizeBalancedTree<NodeOfIp>();
        this.ipRangeTree_T = new SizeBalancedTree<NodeOfIp>();
        this.build();
    }

    /**
     * 建立IP段SBT
     *
     * @param ipRangeTree_S
     * @param ipRangeTree_T
     */
    public void build() {
        try {
            File file = new File(ipRangeFileName);
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);
            String line = "";
            int value = 0;
            String area = "";
            while ((line = reader.readLine()) != null) {
                if (line.charAt(1) >= '0' && line.charAt(1) <= '9') {
                    String[] tempIpRange = line.split("   ");
                    String tempIp_S = tempIpRange[0];
                    String tempIp_T = tempIpRange[1];
                    NodeOfIp nodeIP_S = new NodeOfIp(IpV4Util.ipToInt(tempIp_S),area);
                    NodeOfIp nodeIP_T = new NodeOfIp(IpV4Util.ipToInt(tempIp_T),area);
                    ipRangeTree_S.insert(nodeIP_S);
                    ipRangeTree_T.insert(nodeIP_T);
                }
                else {
                    area = line;
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * 传入IP地址，搜索ipRangeTree的结果，返回省份信息
     *
     * @param checkIp
     * @return ans
     */
    public String search(String checkIp) {
        String ans = "null";
        try {
            //TODO: 继续完善
        }
        catch (Exception e) {
            System.out.println("Err " + e);
        }
        return ans;
    }
}

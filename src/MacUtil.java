import java.lang.Long;

public class MacUtil {

    /**
     * MAC地址转换为Long型
     *
     * @param strMac
     * @return Long
     */
    public static long macToLong(String strMac) {
        long[] mac = new long[6];

        // 先找到MAC地址字符串中:的位置
        int position1 = strMac.indexOf("-");
        int position2 = strMac.indexOf("-", position1 + 1);
        int position3 = strMac.indexOf("-", position2 + 1);
        int position4 = strMac.indexOf("-", position3 + 1);
        int position5 = strMac.indexOf("-", position4 + 1);

        // 将每个:之间的字符串转换成整型
        mac[0] = Long.parseLong(strMac.substring(0, position1),16);
        mac[1] = Long.parseLong(strMac.substring(position1 + 1, position2),16);
        mac[2] = Long.parseLong(strMac.substring(position2 + 1, position3),16);
        mac[3] = Long.parseLong(strMac.substring(position3 + 1, position4),16);
        mac[4] = Long.parseLong(strMac.substring(position4 + 1, position5),16);
        mac[5] = Long.parseLong(strMac.substring(position5 + 1),16);

        return (mac[0] << 40) + (mac[1] << 32) + (mac[2] << 24) + (mac[3] << 16) + (mac[4] << 8) + mac[5];
    }

    /**
     * Long型转换为MAC地址(不补0)
     *
     * @param longMac
     * @return String
     */
    public static String longToMac(long longMac) {
        StringBuffer sb = new StringBuffer("");

        // 直接右移24位
        sb.append(Long.toHexString(longMac >>> 40).toUpperCase());
        sb.append("-");
        // 将高8位置0，然后右移32位
        sb.append(Long.toHexString((longMac >>> 32) & 0x00FF).toUpperCase());
        sb.append("-");
        // 将高16位置0，然后右移24位
        sb.append(Long.toHexString((longMac >>> 24) & 0x0000FF).toUpperCase());
        sb.append("-");
        // 将高24位置0，然后右移16位
        sb.append(Long.toHexString((longMac >>> 16) & 0x000000FF).toUpperCase());
        sb.append("-");
        // 将高32位置0，然后右移8位
        sb.append(Long.toHexString((longMac >>> 8)  & 0x00000000FF).toUpperCase());
        sb.append("-");
        // 将高40位置0
        sb.append(Long.toHexString(longMac & 0x0000000000FF).toUpperCase());

        return sb.toString();
    }
}

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MainDataAnalysis {

    /**
     * 程序主入口
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            //计时器工具初始化
            ProgramTimer pt = new ProgramTimer();
            pt.start();

            //IP分布范围工具初始化
            IpAreaRange ipar = new IpAreaRange(FilePath.ipRangeFile);

            //MAC地址统计工具初始化
            MacAddressStatistics mast = new MacAddressStatistics();

            String pattern = new String();
            //日志格式正则
            pattern = "([^ ]*) ([^ ]*) ([^ ]*) (\\[.*\\]) (\\\".*?\\\") (-|[0-9]*) (-|[0-9]*) (\\\".*?\\\") (\\\".*?\\\") \\\"([^\\\"]*)\\\"";
            Pattern r = Pattern.compile(pattern);

            //读入日志文件
            File file = new File(FilePath.logFile);

            //缓冲流
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));

            //缓冲区大小为5M
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);

            String line = "";

            //处理的记录数
            int dataCount = 0;

            while ((line = reader.readLine()) != null) {
                //TODO: single Thread Task
                //抽取日志文件的一行进行处理
                Matcher m = r.matcher(line);
                if (m.find()) {
                    String temp = ipar.search(m.group(1));
                    mast.insert(m.group(10));
                    dataCount++;
                }
                else {
                    System.out.println("NO MATCH!");
                }
            }
            //输出MAC地址统计结果
            mast.getAnswer();
            //输出省份统计结果
            //TODO:
            System.out.println("Matched Data Counter: " + dataCount);
            //程序运行时间
            System.out.println(pt.runningTime());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
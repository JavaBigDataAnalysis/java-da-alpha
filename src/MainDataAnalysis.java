import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MainDataAnalysis {

    /**
     * 程序主入口-计算核心的串行实现
     *
     * return jsonOfAnswer JSONObject
     */
    public static JSONObject runBySequentialComputing() {
        try {
            //计时器工具初始化
            ProgramTimer pt = new ProgramTimer();
            pt.start();

            //IP分布范围工具初始化
            IpAreaRange ipar = new IpAreaRange();

            //MAC地址统计工具初始化
            MacAddressStatistics mast = new MacAddressStatistics();

            //日志格式正则(NginX日志,txt)
            //pattern = "([^ ]*) ([^ ]*) ([^ ]*) (\\[.*\\]) (\\\".*?\\\") (-|[0-9]*) (-|[0-9]*) (\\\".*?\\\") (\\\".*?\\\") \\\"([^\\\"]*)\\\"";
            //日志格式正则(比赛日志,csv)
            String pattern = "(,)?((\\\"[^\\\"]*(\\\"{2})*[^\\\"]*\\\")*[^,]*)";
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
            int sumDataCount = 0;

            //提取的条件
            String needDate = "2018-07-17";

            //串行实现计算任务
            while ((line = reader.readLine()) != null) {
                //抽取日志文件的一行进行处理
                sumDataCount++;
                //匹配CSV中最小单位的数据
                Matcher matcher = r.matcher(line);
                ArrayList<String> listOfLineData = new ArrayList<String>();
                while(matcher.find()) {
                    String cell = matcher.group(2);//group(2) is ((\"[^\"]*(\"{2})*[^\"]*\")*[^,]*)
                    Pattern pattern2 = Pattern.compile("\"((.)*)\"");
                    Matcher matcher2 = pattern2.matcher(cell);
                    if(matcher2.find()) {
                        cell = matcher2.group(1);
                    }
                    //合并同一行的数据
                    listOfLineData.add(cell);
                }
                //按照匹配条件提取当前行需要的数据
                if (listOfLineData.get(1).equals(needDate)){
                    String region;
                    //该记录存在多个IP地址
                    if (listOfLineData.get(5).contains(",")){
                        String[] tmp = listOfLineData.get(5).split(", ");
                        region = ipar.search(tmp[tmp.length-1]);
                    }
                    else {
                        region = ipar.search(listOfLineData.get(5));
                    }
                    //该记录不存在MAC地址
                    if (listOfLineData.get(4).isEmpty()){
                        mast.insert("00-00-00-00-00-00");
                    }
                    else {
                        mast.insert(listOfLineData.get(4));
                    }
                    dataCount++;
                }
            }

            //输出匹配记录数
            System.out.println("Matched/Summary Data Counter: " + dataCount + "/" + sumDataCount);

            //程序运行时间
            System.out.println("串行计算" + pt.runningTime());

            //将结果转换为JSON传出
            JSONArray arrayOfMac =  JSONArray.fromObject(mast.getAnswer());
            JSONArray arrayOfIp =  JSONArray.fromObject(ipar.getRegionCount());
            JSONObject jsonOfAnswer = new JSONObject();
            jsonOfAnswer.put("mac",arrayOfMac);
            jsonOfAnswer.put("prov",arrayOfIp);
            jsonOfAnswer.put("Matched",dataCount);
            jsonOfAnswer.put("Summary",sumDataCount);
            jsonOfAnswer.put("RunningTime:",pt.runningTime());
            return jsonOfAnswer;

        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * 程序主入口-计算核心的并行实现-线程安全
     *
     * return jsonOfAnswer JSONObject
     */
    public static JSONObject runByParallelComputing() {
        try {
            //计时器工具初始化
            ProgramTimer pt = new ProgramTimer();
            pt.start();

            //IP分布范围工具初始化
            IpAreaRange ipar = new IpAreaRange();

            //MAC地址统计工具初始化
            MacAddressStatistics mast = new MacAddressStatistics();

            //日志格式正则(NginX日志,txt)
            //pattern = "([^ ]*) ([^ ]*) ([^ ]*) (\\[.*\\]) (\\\".*?\\\") (-|[0-9]*) (-|[0-9]*) (\\\".*?\\\") (\\\".*?\\\") \\\"([^\\\"]*)\\\"";
            //日志格式正则(比赛日志,csv)
            String pattern = "(,)?((\\\"[^\\\"]*(\\\"{2})*[^\\\"]*\\\")*[^,]*)";

            //正则匹配初始化
            Pattern r = Pattern.compile(pattern);

            //读入日志文件
            File file = new File(FilePath.logFile);

            //缓冲流
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));

            //缓冲区大小为5M
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);

            //处理的记录数(总记录数)
            int[] testSumDataCount = {0};

            //处理的记录数(按照条件匹配到的记录数)
            int[] testDataCount = {0};

            //提取的条件
            String needDate = "2018-07-17";

            //线程锁
            Lock lock = new ReentrantLock();

            //根据物理机CPU核心数自动判断可以开启的线程数量,并行处理计算任务
            reader.lines().parallel().forEach((String line) -> {
                //每个线程抽取日志文件的一行进行处理
                //原子操作使用线程锁确保线程安全
                lock.lock();
                try{
                    testSumDataCount[0] ++ ;
                }
                finally {
                    lock.unlock();
                }
                //匹配CSV中最小单位的数据
                Matcher matcher = r.matcher(line);
                List<String> listOfLineData = Collections.synchronizedList(new ArrayList<String>());
                while(matcher.find()) {
                    String cell = matcher.group(2);//group(2) is ((\"[^\"]*(\"{2})*[^\"]*\")*[^,]*)
                    Pattern pattern2 = Pattern.compile("\"((.)*)\"");
                    Matcher matcher2 = pattern2.matcher(cell);
                    if(matcher2.find()) {
                        cell = matcher2.group(1);
                    }
                    //合并同一行的数据
                    listOfLineData.add(cell);
                }
                //按照匹配条件提取当前行需要的数据
                if (listOfLineData.get(1).equals(needDate)){
                    String region;
                    String infoOfIp;
                    String infoOfMac;
                    //该记录存在多个IP地址
                    if (listOfLineData.get(5).contains(",")){
                        String[] tmp = listOfLineData.get(5).split(", ");
                        infoOfIp = tmp[tmp.length-1];
                    }
                    else {
                        infoOfIp = listOfLineData.get(5);
                    }
                    //该记录不存在MAC地址
                    if (listOfLineData.get(4).isEmpty()){
                        infoOfMac = "00-00-00-00-00-00";
                    }
                    else {
                        infoOfMac = listOfLineData.get(4);
                    }
                    //对取得的数据进行记录(线程安全)
                    lock.lock();
                    try{
                        ipar.search(infoOfIp);
                        mast.insert(infoOfMac);
                        testDataCount[0]++;
                    }
                    finally {
                        lock.unlock();
                    }
                }
            });

            //输出匹配记录数
            System.out.println("Matched/Summary Data Counter: " + testDataCount[0] + "/" + testSumDataCount[0]);

            //程序运行时间
            System.out.println("并行计算" + pt.runningTime());

            //将结果转换为JSON传出
            JSONArray arrayOfMac = JSONArray.fromObject(mast.getAnswer());
            JSONArray arrayOfIp = JSONArray.fromObject(ipar.getRegionCount());
            JSONObject jsonOfAnswer = new JSONObject();
            jsonOfAnswer.put("mac",arrayOfMac);
            jsonOfAnswer.put("prov",arrayOfIp);
            jsonOfAnswer.put("Matched",testDataCount[0]);
            jsonOfAnswer.put("Summary",testSumDataCount[0]);
            jsonOfAnswer.put("RunningTime:",pt.runningTime());
            return jsonOfAnswer;

        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * 程序主入口-计算核心的并行实现-非线程安全
     *
     * return jsonOfAnswer JSONObject
     */
    public static JSONObject runByParallelComputingNotSafe() {
        try {
            //计时器工具初始化
            ProgramTimer pt = new ProgramTimer();
            pt.start();

            //IP分布范围工具初始化
            IpAreaRange ipar = new IpAreaRange();

            //MAC地址统计工具初始化
            MacAddressStatistics mast = new MacAddressStatistics();

            //日志格式正则(NginX日志,txt)
            //pattern = "([^ ]*) ([^ ]*) ([^ ]*) (\\[.*\\]) (\\\".*?\\\") (-|[0-9]*) (-|[0-9]*) (\\\".*?\\\") (\\\".*?\\\") \\\"([^\\\"]*)\\\"";
            //日志格式正则(比赛日志,csv)
            String pattern = "(,)?((\\\"[^\\\"]*(\\\"{2})*[^\\\"]*\\\")*[^,]*)";

            //正则匹配初始化
            Pattern r = Pattern.compile(pattern);

            //读入日志文件
            File file = new File(FilePath.logFile);

            //缓冲流
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));

            //缓冲区大小为5M
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);

            //处理的记录数(总记录数)
            int[] testSumDataCount = {0};

            //处理的记录数(按照条件匹配到的记录数)
            int[] testDataCount = {0};

            //提取的条件
            String needDate = "2018-07-17";

            //线程锁
            Lock lock = new ReentrantLock();

            //根据物理机CPU核心数自动判断可以开启的线程数量,并行处理计算任务
            reader.lines().parallel().forEach((String line) -> {
                //每个线程抽取日志文件的一行进行处理
                //原子操作使用线程锁确保线程安全
                lock.lock();
                try{
                    testSumDataCount[0] ++ ;
                }
                finally {
                    lock.unlock();
                }
                //匹配CSV中最小单位的数据
                Matcher matcher = r.matcher(line);
                List<String> listOfLineData = Collections.synchronizedList(new ArrayList<String>());
                while(matcher.find()) {
                    String cell = matcher.group(2);//group(2) is ((\"[^\"]*(\"{2})*[^\"]*\")*[^,]*)
                    Pattern pattern2 = Pattern.compile("\"((.)*)\"");
                    Matcher matcher2 = pattern2.matcher(cell);
                    if(matcher2.find()) {
                        cell = matcher2.group(1);
                    }
                    //合并同一行的数据
                    listOfLineData.add(cell);
                }
                //按照匹配条件提取当前行需要的数据
                if (listOfLineData.get(1).equals(needDate)){
                    String region;
                    String infoOfIp;
                    String infoOfMac;
                    //该记录存在多个IP地址
                    if (listOfLineData.get(5).contains(",")){
                        String[] tmp = listOfLineData.get(5).split(", ");
                        infoOfIp = tmp[tmp.length-1];
                    }
                    else {
                        infoOfIp = listOfLineData.get(5);
                    }
                    //该记录不存在MAC地址
                    if (listOfLineData.get(4).isEmpty()){
                        infoOfMac = "00-00-00-00-00-00";
                    }
                    else {
                        infoOfMac = listOfLineData.get(4);
                    }
                    ipar.search(infoOfIp);
                    mast.insert(infoOfMac);
                    lock.lock();
                    try{

                        testDataCount[0]++;
                    }
                    finally {
                        lock.unlock();
                    }
                }
            });

            //输出匹配记录数
            System.out.println("Matched/Summary Data Counter: " + testDataCount[0] + "/" + testSumDataCount[0]);

            //程序运行时间
            System.out.println("非安全并行计算" + pt.runningTime());

            //将结果转换为JSON传出
            JSONArray arrayOfMac = JSONArray.fromObject(mast.getAnswer());
            JSONArray arrayOfIp = JSONArray.fromObject(ipar.getRegionCount());
            JSONObject jsonOfAnswer = new JSONObject();
            jsonOfAnswer.put("mac",arrayOfMac);
            jsonOfAnswer.put("prov",arrayOfIp);
            jsonOfAnswer.put("Matched",testDataCount[0]);
            jsonOfAnswer.put("Summary",testSumDataCount[0]);
            jsonOfAnswer.put("RunningTime:",pt.runningTime());
            return jsonOfAnswer;

        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * 获取系统性能信息的入口函数(注意!仅支持Windows系统和Linux系统,不支持MacOS)
     *
     * @return jsonOfAnswer JSONObject
     */
    public static JSONObject getSystemInfo() throws Exception {
        //当前系统的CPU使用率
        double cpuUsage = SystemInfo.getCpuUsage();
        //当前系统的内存使用率
        double memUsage = SystemInfo.getMemUsage();
        //当前系统的硬盘使用率
        double diskUsage = SystemInfo.getDiskUsage();
        JSONObject jsonOfAnswer = new JSONObject();
        jsonOfAnswer.put("CPU",Integer.toString(
                Integer.parseInt(new java.text.DecimalFormat("0").format(cpuUsage))));
        return jsonOfAnswer;
    }

    public static void main(String[] args){
        //System.out.println(MainDataAnalysis.runBySequentialComputing());
        //System.out.println(MainDataAnalysis.runByParallelComputing());
        System.out.println(MainDataAnalysis.runByParallelComputingNotSafe());
    }
}
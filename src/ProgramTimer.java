public class ProgramTimer {
    //程序的开始时间
    private long startTime;
    //程序的结束时间
    private long endTime;

    public ProgramTimer () {
        this.startTime = 0;
        this.endTime = 0;
    }

    /**
     * 开始记录程序运行时间
     *
     * @param startTime Long Integer
     */
    public void start () {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 返回程序的运行时间
     *
     * @return String
     */
    public String runningTime () {
        this.endTime = System.currentTimeMillis();
        return formatTime(endTime-startTime);
    }

    /**
     * 对标准时的转换
     *
     * @param ms Long
     * @return 程序总用时:X天X小时X分X秒X毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        sb.append("程序总用时：");
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond+"毫秒");
        }
        return sb.toString();
    }

}

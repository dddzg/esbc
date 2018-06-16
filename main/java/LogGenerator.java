import java.io.OutputStream;
import java.net.URI;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class LogGenerator {

    private static String timeFormat = "yyyy-MM-dd HH:mm:ss";
    private static Calendar calendar = new GregorianCalendar();
    /**
     * 一年的秒数 365*24*60*60
     */
    private static int secCount = 7 * 24 * 60 * 60;
    /**
     * 8000000个用户
     */
    private static int userCount = 8000000;
    private static Random random = new Random(100);
    private static Set loginedUser = new HashSet();

    public static OutputStream getOutputStream(String filePath) throws Exception {
        Configuration conf = new Configuration();
        // 不设置该代码会出现错误：java.io.IOException: No FileSystem for scheme: hdfs
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

        Path path = new Path(filePath);
        // 这里需要设置URI，否则出现错误：java.lang.IllegalArgumentException: Wrong FS: hdfs://127.0.0.1:9000/test/test.txt, expected: file:///
        FileSystem fs = FileSystem.get(new URI(filePath), conf);

        System.out.println("WRITING ============================");

        FSDataOutputStream os = fs.create(path);
        return os;
    }

    public static void main(String[] args) throws Exception {
        String filePath = "hdfs://master:9000/pratice/test-day.dat";
        OutputStream outputStream = getOutputStream(filePath);
//        OutputStream outputStream = System.out;
        Date startDate = strToDateLong("2017-01-01 00:00:00");
        calendar.setTime(startDate);
        for (int i = 0; i <= secCount; i++) {
            Date date = calendar.getTime();
            for (int count = 0; count <= 160; count++) {
                int userId = Math.abs(random.nextInt()) % userCount;
                if (loginedUser.contains(userId)) {
                    String str =
                        getStringDate(date) + ';' + String.format("%08d", userId) + ';' + "1\n";
                    outputStream.write(str.getBytes());
                    loginedUser.remove(userId);
                } else {
                    String str =
                        getStringDate(date) + ';' + String.format("%08d", userId) + ';' + "0\n";
                    outputStream.write(str.getBytes());
                    loginedUser.add(userId);
                }
            }
            if (i % (24 * 60 * 60) == 0) {
                System.out.println(date);
            }
            calendar.add(Calendar.SECOND, 1);
        }
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
        String dateString = formatter.format(date);
        return dateString;
    }
}

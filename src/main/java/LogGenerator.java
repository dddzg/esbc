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

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 一年的秒数 365*24*60*60
     */
    private static final int SEC_COUNT = 7 * 24 * 60 * 60;
    /**
     * 8000000个用户
     */
    private static final int USER_COUNT = 8000000;
    private static final int LOG_PER_SEC = 160;
    
    private static final String LOG_START_TIME = "2017-01-01 00:00:00";

    private static final Random random = new Random(233);
    private static final Set loginedUser = new HashSet();
    private static final Calendar calendar = new GregorianCalendar();

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
        String filePath = args[0];
        OutputStream outputStream = getOutputStream(filePath);
//        OutputStream outputStream = System.out;
        Date startDate = strToDateLong(LOG_START_TIME);
        calendar.setTime(startDate);
        for (int i = 0; i <= SEC_COUNT; i++) {
            Date date = calendar.getTime();
            for (int count = 0; count <= LOG_PER_SEC; count++) {
                int userId = Math.abs(random.nextInt()) % USER_COUNT;
                String str = getStringDate(date) + ';' + String.format("%08d", userId) + ';'
                if (loginedUser.contains(userId)) {
                    str += "1\n";
                    outputStream.write(str.getBytes());
                    loginedUser.remove(userId);
                } else {
                    str += "0\n";
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
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
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
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        String dateString = formatter.format(date);
        return dateString;
    }
}

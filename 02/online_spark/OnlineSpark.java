
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import org.apache.hadoop.conf.Configuration;

import java.net.URI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @program: OnlineSpark
 * @author: zyd
 * @description: cal per time by spark
 * @create: 2018-06-22 15:13
 */
public class OnlineSpark {

    //    日志path
    private final static String FILE_PATH = "hdfs://master:9000/random/logs.bat";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String OUTPUT_PATH = "hdfs://master:9000/user/hadoop/spark_online.txt";

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: OnlineSpark <file>");
            System.exit(1);
        }

        SparkConf sparkConf = new SparkConf().setAppName("OnlineSpark");
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
        JavaRDD<String> lines = ctx.textFile(FILE_PATH);

        JavaPairRDD<String, String> userTimeLog = lines.mapToPair(new PairFunction<String, String, String>() {

            public Tuple2<String, String> call(String s) {

                String[] items = s.split(";");
                return new Tuple2<String, String>(items[1], items[0] + ";" + items[2]);
            }
        });

        JavaPairRDD<String, Double> perUserTime = userTimeLog.groupByKey().mapToPair(new PairFunction<Tuple2<String, Iterable<String>>, String, Double>() {
            public Tuple2<String, Double> call(Tuple2<String, Iterable<String>> input) throws Exception {
                double sumTime = 0.00;
                int loginTimes = 0;
                Iterable<String> value = input._2;

                String[] temp;
                ArrayList<Long> times = new ArrayList<Long>();

                for (String time : value) {
                    temp = time.split(";");
                    try {
                        if (temp[0].length() != 0) {
                            times.add(DATE_FORMAT.parse(temp[0]).getTime());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(times);
                for (int i = 0; i < times.size() - 1; i += 2) {
                    sumTime += (times.get(i + 1) - times.get(i));
                    loginTimes += 1;
                }
                double perUserTime = sumTime / (loginTimes * 1000);
                return new Tuple2<String, Double>("perUserTime", perUserTime);
            }
        });

        final JavaPairRDD<String, Double> counts = perUserTime.reduceByKey(new Function2<Double, Double, Double>() {
            public Double call(Double i1, Double i2) {
                return (i1 + i2);
            }
        });

        double num = 0.00;
        List<Tuple2<String, Double>> output = counts.collect();
        for (Tuple2<String, Double> tuple : output) {
            num = num + tuple._2;
        }
        double result = num / (double) counts.keys().count();

        String temp = "平均每个用户的登陆时间：" + result;

        System.out.println(temp);
//        writeOutput(temp);
        
        ctx.stop();
    }

    private static void writeOutput(String temp) throws Exception{
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

        Path path = new Path(OUTPUT_PATH);

        FileSystem fs = FileSystem.get(new URI(OUTPUT_PATH), conf);

        byte[] buff = temp.getBytes();
        FSDataOutputStream os = fs.create(path);
        os.write(buff, 0, buff.length);
        os.close();

        fs.close();
    }
}

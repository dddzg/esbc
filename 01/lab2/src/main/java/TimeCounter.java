import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TimeCounter {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "time counter");
        job.setJarByClass(TimeCounter.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        System.out.println(args[0]);
        System.out.println(args[1]);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

    public static class TokenizerMapper
        extends Mapper<Object, Text, Text, Text> {

        private Text userId = new Text();
        private Text status = new Text();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString(), "\n");
            while (itr.hasMoreTokens()) {
                String line = itr.nextToken().trim();
                if (!line.isEmpty()) {
                    String[] arr = line.split(";");
                    userId.set(arr[1]);
                    status.set(arr[0] + ";" + arr[2]);
                    context.write(userId, status);
                }
            }
        }
    }

    public static class IntSumReducer
        extends Reducer<Text, Text, Text, Text> {

        private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        private Text result = new Text();

        public static Date strToDateLong(String strDate) {
            SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
            ParsePosition pos = new ParsePosition(0);
            Date strtodate = formatter.parse(strDate, pos);
            return strtodate;
        }

        public void reduce(Text key, Iterable<Text> values,
            Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            int times = 0;
            ArrayList<Long> arrayList = new ArrayList<>();
            for (Text val : values) {
                String line = val.toString();
                if (!line.isEmpty()) {
                    try {
                        String[] arr = line.split(";");
                        Date nowTime = strToDateLong(arr[0]);
                        if (nowTime != null) {
                            arrayList.add(nowTime.getTime());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.print(line.split(";")[0] + "dzg!" + line + "\n");
                    }
                }
            }
            Collections.sort(arrayList);
            for (int i = 0; i < arrayList.size(); i += 2) {
                if (i + 1 < arrayList.size()) {
                    sum += (arrayList.get(i + 1) - arrayList.get(i)) / 1000;
                    times += 1;
                }
            }
            String str =
                String.valueOf(sum * 1.0 / times) + ";" + String.valueOf(sum) + ";" + String
                    .valueOf(times);
            result.set(str);
            context.write(key, result);
        }
    }
}
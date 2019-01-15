package bid.adonis.lau.hadoop.ncdc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 统计每年最高气温
 * Combiner的作用就是对map输出的结果先做一次合并
 * Combiner的存在就是提高当前网络IO传输的性能，也是MapReduce的一种优化手段
 *
 * @author adonis
 */
public class MaxTemperatureWithCombiner {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://mac.adonis-lau.bid:8020");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        FileSystem fs = FileSystem.get(conf);

        Job job = Job.getInstance(conf);
        job.setJarByClass(MaxTemperatureWithCombiner.class);
        job.setJobName("Max temperature");

        FileInputFormat.addInputPath(job, new Path("learn/hadoop-book/input/ncdc/all"));
        Path outputPath = new Path("learn/hadoop-book/input/ncdc/all/output");
        if (fs.exists(outputPath)) {
            // 如果目录存在，则删除
            fs.delete(outputPath, true);
        }
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setMapperClass(MaxTemperatureMapper.class);
        /*[*/
        job.setCombinerClass(MaxTemperatureReducer.class)/*]*/;
        job.setReducerClass(MaxTemperatureReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

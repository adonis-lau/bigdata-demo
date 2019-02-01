package bid.adonis.lau.hadoop.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * @author: Adonis Lau
 * @date: 2018/12/29 18:48
 */
public class WordCount {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
//        conf.set("fs.defaultFS", "hdfs://mac.adonis-lau.bid:8020");
        conf.set("fs.defaultFS", "hdfs://bigdata.shadowsocks.tech:8020");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");


        // 创建job对象
        Job job = Job.getInstance(conf);
        // 指定程序的入口
        job.setJarByClass(WordCount.class);
        //作业名称
        job.setJobName("WordCount");
        // 指定自定义的Mapper阶段的任务处理类
        job.setMapperClass(WordCountMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        // 数据HDFS文件服务器读取数据路径
        FileInputFormat.setInputPaths(job, new Path("learn/test-files/word-count/words.txt"));
        /*FileInputFormat.addInputPath(job, new Path(args[0]));*/
        // 指定自定义的Reducer阶段的任务处理类
        job.setReducerClass(WordCountReducer.class);
        // 设置最后输出结果的Key和Value的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        // 将计算的结果上传到HDFS服务
        FileOutputFormat.setOutputPath(job, new Path("learn/test-files/word-count/result/" + System.currentTimeMillis()));
        /*FileOutputFormat.setOutputPath(job, new Path(args[1]));*/
        // 执行提交job方法，直到完成，参数true打印进度和详情
        job.waitForCompletion(true);
        System.out.println("Finished");
        System.out.println("测试完成");
    }
}

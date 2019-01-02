package bid.adonis.lau.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author: Adonis Lau
 * @date: 2018/12/29 18:48
 */
@Slf4j
public class Hadoop {
    Configuration conf = null;
    FileSystem fs = null;

    @Before
    public void init() throws IOException {
        conf = new Configuration();
//        conf.set("fs.defaultFS", "hdfs://localhost:8020");
        conf.set("fs.defaultFS", "hdfs://mac.shadowsocks.tech:8020");
//        conf.set("fs.defaultFS", "hdfs://139.227.6.22:8020");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");

        fs = FileSystem.get(conf);
    }

    @Test
    public void getFileList() throws IOException {
        Path path = new Path("/hbase");
        boolean exists = fs.exists(path);
        System.out.println(exists);
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(path, true);
        while (listFiles.hasNext()){
            LocatedFileStatus fileStatus = listFiles.next();
            System.out.println(fileStatus.getPath());
        }
    }
}

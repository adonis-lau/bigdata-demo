package bid.adonis.lau.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        conf.set("fs.defaultFS", "hdfs://mac.adonis-lau.bid:8020");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
//        System.setProperty("hadoop.home.dir", "D:\\ProgramFiles\\Apache\\hadoop-2.6.0");

        fs = FileSystem.get(conf);
    }

    @Test
    public void getFileList() throws IOException {
        Path path = new Path("/");
        boolean exists = fs.exists(path);
        System.out.println(exists);
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(path, true);
        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();
            System.out.println(fileStatus.getPath());
        }
    }

    @Test
    public void getFileGlobStatus() throws IOException {
        final String[] filterFileNames = new String[]{"_SUCCESS", "_FAILURE"};
        final String[] filterFilePathNames = new String[]{"_logs"};

        PathFilter pathFilter = path -> {
            for (String filterFileName : filterFileNames) {
                if (path.getName().equals(filterFileName)) {
                    return false;
                }
            }
            for (String filterFilePathName : filterFilePathNames) {
                if (path.getParent().getName().equals(filterFilePathName)) {
                    return false;
                }
            }
            return true;
        };

        Path path = new Path("/hbase/data/hbase/namespace/da3b71f05bd087ff18f324fe86574153/recovered.edits");
        List<FileStatus> children = getChildren(path.toString(), pathFilter);
        for (FileStatus child : children) {
            System.out.println(child.getPath().toUri().toString());
        }

    }

    public List<FileStatus> getChildren(String hdfsPath, PathFilter pathFilter) throws IOException {
        /*
        FileStatus[] fileStatuses = hadoopVFS.fs.globStatus(new Path(hdfsPath), pathFilter);
        return Arrays.asList(fileStatuses);
        */


        FileStatus[] fileStatuses = fs.globStatus(new Path(hdfsPath), pathFilter);
        List<FileStatus> retList = new ArrayList<FileStatus>();
        for (int i = 0; i < fileStatuses.length; i++) {
            List<FileStatus> list = getChildren(fileStatuses[i], pathFilter);
            retList.addAll(list);
        }
        return retList;
    }

    private List<FileStatus> getChildren(FileStatus fileStatus, PathFilter pathFilter) throws IOException {
        //hadoopVFS.LOGGER.logDebug("fileStatus"+ fileStatus.getPath());
        FileStatus[] fileStatuses = fs.listStatus(fileStatus.getPath(), pathFilter);
        //hadoopVFS.LOGGER.logDebug("filelength"+ fileStatuses.length);
        if (fileStatuses.length == 1 && !fileStatuses[0].isDirectory()) {
            //hadoopVFS.LOGGER.logDebug("fileStatuses[0]"+ fileStatuses[0].getPath());
            return Arrays.asList(fileStatuses[0]);
        }
        List<FileStatus> retList = new ArrayList<FileStatus>();
        for (int i = 0; i < fileStatuses.length; i++) {
            List<FileStatus> list = getChildren(fileStatuses[i], pathFilter);
            retList.addAll(list);
        }
        return retList;
    }

}

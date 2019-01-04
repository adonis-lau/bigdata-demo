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

import java.io.FileNotFoundException;
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

//        Path path = new Path("/hbase/data/hbase/namespace/da3b71f05bd087ff18f324fe86574153/recovered.edits");
        Path path = new Path("/learn/test-files/word-count/words.txt");
        FileStatus[] fileStatuses = fs.globStatus(path);
        System.out.println(fileStatuses.length);
        System.out.println(fileStatuses[0]);
        List<FileStatus> children = getChildren(path.toString(), pathFilter);
        if (isDirectory(path.toString())) {
            System.out.println("is directory");
        }
//        System.out.println(children.size() < 1);

    }

    public List<FileStatus> getChildren(String hdfsPath, PathFilter pathFilter) throws IOException {
        /*
        FileStatus[] fileStatuses = hadoopVFS.fs.globStatus(new Path(hdfsPath), pathFilter);
        return Arrays.asList(fileStatuses);
        */


        FileStatus[] fileStatuses = fs.listStatus(new Path(hdfsPath), pathFilter);
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


    /**
     * 根据hdfs路径获得路径的状态
     * num == 1 ，不是目录
     *
     * @param hdfsPath
     * @return
     * @throws java.io.IOException
     */
    public int getFileStatus(String hdfsPath) throws IOException {
        FileStatus[] fileStatus = fs.globStatus(new Path(hdfsPath));
        if (fileStatus == null || fileStatus.length == 0) {
            throw new FileNotFoundException("Cannot access " + hdfsPath + ": No such file or directory.");
        }
        int num = 0;
        for (int i = 0; i < fileStatus.length; i++) {
            num += getFileStatus(fileStatus[i]);
        }
        return num;
    }

    /**
     * 判断hdfs路径是否是目录
     *
     * @param hdfsPath
     * @return
     * @throws java.io.IOException
     */
    public boolean isDirectory(String hdfsPath) throws IOException {
        int num = getFileStatus(hdfsPath);
        return num > 0;
    }

    private int getFileStatus(FileStatus fileStatus) throws IOException {
        //hadoopVFS.LOGGER.logDebug("status:" + fileStatus.getPath());
        FileStatus[] fileStatuses = fs.listStatus(fileStatus.getPath());
        //hadoopVFS.LOGGER.logDebug(fileStatuses == null ? "null" : "not null"+fileStatuses.length);
        if (fileStatuses.length == 1 && !fileStatuses[0].isDirectory()) {
            //hadoopVFS.LOGGER.logDebug("fileStatus[0]"+ fileStatuses[0].getPath());
            return 1;
        }
        int num = 0;
        for (int i = 0; i < fileStatuses.length; i++) {
            num += getFileStatus(fileStatuses[i]);
        }
        return num;
    }
}

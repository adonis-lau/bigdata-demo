##WordCount
####1、在/home路径下，新建words.txt文档，文档内容如下：

```text
hello tom
hello jerry
hello kitty
hello world
hello tom
```
 
####2、上传到hdfs文件服务器/hadoop目录下：
执行命令：
```text
hadoop fs -put /home/words.txt learn/test-files/word-count/words.txt
hadoop fs -cat learn/test-files/word-count/words.txt
```

####3、将程序打成Jar包
使用maven命令将程序打包<br/>

####4、运行JAR        
执行JAR包命令：
```text
hadoop jar hadoop-demo.jar bid.adonis.lau.hadoop.wordcount.WordCount
```
 
####5、查看执行结果
执行命令：
```text
hadoop fs -ls learn/test-files/word-count/result/1546101492383
hadoop fs -cat learn/test-files/word-count/result/1546101492383/part-r-00000

```

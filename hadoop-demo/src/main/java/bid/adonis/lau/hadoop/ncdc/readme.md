##计算每一年最高气温
####1、上传文件

```text
hadoop fs -put * /learn/hadoop-book/input/ncdc/all/
```

####3、将程序打成Jar包
使用maven命令将程序打包<br/>

####4、运行JAR        
执行JAR包命令：
```text
hadoop jar hadoop-demo.jar bid.adonis.lau.hadoop.ncdc.MaxTemperature
```
 
####5、查看执行结果
执行命令：
```text
hadoop fs -cat hadoop fs -text /learn/hadoop-book/input/ncdc/all/output/part-r-00000
```

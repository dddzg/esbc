1.讨论：日志格式要怎么定义？

​		根据GMT时间，userID，登入登出的标志来定义日志。

​		GMT时间：10 06 2018 13:15:17 最小单位为秒

​		userID 由要管理800万个用户来得出选用8位0-9的数字来生成userID

​		登入登出用 0/1来表示

   		由这三个字段来构成，不同字段之间用;来分割

​      		例如：2018-06-10 13:15:17;12345678;0		

2. 讨论：关于50亿条记录，800个用户是一个什么样的数量级？

​		50亿条记录，800万个用户即平均每个用户生成625条记录

​		如果时间跨度为一年，平均每秒158.5条记录

​		大约是一年内每个用户每天登录登出一次。

3. 用MapReduce来解决计算机分布式问题，map函数用于大数据的输入，并将其分成小片段，然后交给其他进程进行操作，reduce函数整理map收集的各个会用，然后显示最后的输入。

4. HDFS java读写接口Java抽象类org.apache.hadoop.fs.FileSystem定义了hadoop的一个文件系统接口。

   该类是一个抽象类，通过以下两种静态工厂方法可以过去FileSystem实例：  

   ```java
   public static FileSystem.get(Configuration conf) throws IOException  
   public static FileSystem.get(URI uri, Configuration conf) throws IOException 
   ```

   具体方法实现：  

   1、一次性新建所有目录（包括父目录）， f是完整的目录路径。 

   ```java
   public boolean mkdirs(Path f) throws IOException   
   ```

    2、创建指定path对象的一个文件，返回一个用于写入数据的输出流  create()有多个重载版本，允许我们指定是否强制覆盖已有的文件、文件备份数量、写入文件缓冲区大小、文件块大小以及文件权限。  

   ```java
   public FSOutputStream create(Path f) throws IOException  
   ```

   3、将本地文件拷贝到文件系统  

   ```java
   public boolean copyFromLocal(Path src, Path dst) throws IOException  
   ```

   4、检查文件或目录是否存在   

   ```java
   public boolean exists(Path f) throws IOException  
   ```

   5、永久性删除指定的文件或目录，如果f是一个空目录或者文件，那么recursive的值就会被忽略。只有recursive＝true时，一个非空目录及其内容才会被删除。   

   ```java
   public boolean delete(Path f, Boolean recursive)  
   ```

   6、FileStatus类封装了文件系统中文件和目录的元数据，包括文件长度、块大小、备份、修改时间、所有者以及权限信息。  通过"FileStatus.getPath（）"可查看指定HDFS中某个目录下所有文件。 

5. 讨论：如何随机生成日志

   上述得出：如果时间跨度为一年，那么平均每秒有158条日志

   为了节省内存，系统维护一个set表

   当有一条新的记录进来，查找set的表是否有包含了新纪录的userID

   如果包含，就delete表中的信息，并输出登出记录到日志中

   如果没有包含，就push新纪录到set表中，并输出登入记录到日志中

   这样一个set表的大小是小于800万条数据

6. 讨论：如何确保时间是有序的

   遍历一年中的每一秒，对于每一秒都随机生成相应的用户。

   

   

   
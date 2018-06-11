

# 项目计划和设计文档

## 需求说明书

在一周内完成hadoop集群内的机器和角色分配矩阵，实现hadoop集群的搭建。分配十四台机器，十二台作为slaver，一台作为master， 一台作为master-standby。

### 计划说明

在一周内完成十四台机器的hadoop集群配置.

#### 1.选定一台机器作为master;

#### 2.在master节点上配置hadoop用户、安装ssh server、安装java环境;

#### 3.在其他slave节点上配置hadoop用户，然后通过master机器复制/usr/local/hadoop到slave节点并share配置环境脚本;

#### 4.在其他slave节点上执行配置环境脚本，实现master连接slave节点;

#### 5.实现standby节点的配置;

#### 6. 通过zookeeper实现master和master-standby的热切换。

总共搭了13台台式机的集群，其中一台作为master，一台作为master-standby，10台作为slaver，其中一台联网有故障，没有被考虑使用。

我们组四名成员花了一晚上时间试了单机版hadoop，金花同学在深入了解ssh及其相关问题。用了一个小时对每台机进行初始化，给每台机一个干净的运行环境，用一个晚上时间写了脚本，配了每台机的hadoop，

## 已有知识准备

戴神和金花用过Hadoop，大家都学过Java，章鱼对Java有深入了解，大家都熟悉Linux的各种指令，大家都有Github。

## 需要做的事
1. 尝试单机版Hadoop集群的配置，并深入了解ssh及其相关问题；
2. 对机器进行初始化工作，包括新建hadoop账号防止污染生产环境，关闭防火墙，开启22端口实现远程连接，修改hostname保证集群名称语义化。
3. 配置单机生产环境，包括安装统一版本的Java和Hadoop，分配master和master-standby的公钥实现无密码登录；
4. 配置集群/分布式环境，设置正常启动所必需的设置项：slaves, core-site.xml, hdfs-site.xml等；
5. 删除所有机器上的临时文件和日志文件，格式化HDFS，然后启动Hadoop。

## 安装脚本
1. add_ssh_key.sh  功能：把master和master_standby的公钥加入到slave的authorized_keys中
2. setup.sh 功能：安装jdk和hadoop并修改环境变量；

## 详细步骤
### 创建Hadoop用户

* slave打开终端，输入如下命令创建新用户

  ```
  sudo useradd -m hadoop -s /bin/bash     # 创建hadoop用户
  sudo passwd hadoop          # 修改密码
  sudo adduser hadoop sudo    # 增加管理员权限
  ```

* slave打开22端口

* ```
  service sshd start
  netstat -antulp | grep ssh #查询是否开启
  ```

* slave修改hostname

  ```
  sudo gedit /etc/hostname
  ```

* 重启

### 在Master节点上进行Hadoop集群配置

```
cd  /usr/local/hadoop/etc/hadoop
```

* 文件 **core-site.xml**:

* ```
  <configuration>
   <!-- 指定hdfs的nameservice为ns -->
   <property>    
        <name>fs.defaultFS</name>    
        <value>hdfs://ns</value>    
   </property>
   <!--指定hadoop数据临时存放目录-->
   <property>
        <name>hadoop.tmp.dir</name>
        <value>/home/hadoop/workspace/hdfs/temp</value>
   </property>   
                            
   <property>    
        <name>io.file.buffer.size</name>    
        <value>4096</value>    
   </property>
   <!--指定zookeeper地址-->
   <property>
        <name>ha.zookeeper.quorum</name>
        <value>mast1:2181,mast2:2181,mast3:2181</value>
   </property>
   </configuration>
  ```

* 文件 **hdfs-site.xml**:

* ```
  <configuration>
      <!--指定hdfs的nameservice为ns，需要和core-site.xml中的保持一致 -->    
      <property>    
          <name>dfs.nameservices</name>    
          <value>ns</value>    
      </property>  
      <!-- ns下面有两个NameNode，分别是nn1，nn2 -->
      <property>
         <name>dfs.ha.namenodes.ns</name>
         <value>nn1,nn2</value>
      </property>
      <!-- nn1的RPC通信地址 -->
      <property>
         <name>dfs.namenode.rpc-address.ns.nn1</name>
         <value>mast1:9000</value>
      </property>
      <!-- nn1的http通信地址 -->
      <property>
          <name>dfs.namenode.http-address.ns.nn1</name>
          <value>mast1:50070</value>
      </property>
      <!-- nn2的RPC通信地址 -->
      <property>
          <name>dfs.namenode.rpc-address.ns.nn2</name>
          <value>mast2:9000</value>
      </property>
      <!-- nn2的http通信地址 -->
      <property>
          <name>dfs.namenode.http-address.ns.nn2</name>
          <value>mast2:50070</value>
      </property>
      <!-- 指定NameNode的元数据在JournalNode上的存放位置 -->
      <property>
           <name>dfs.namenode.shared.edits.dir</name>
           <value>qjournal://mast1:8485;mast2:8485;mast3:8485/ns</value>
      </property>
      <!-- 指定JournalNode在本地磁盘存放数据的位置 -->
      <property>
            <name>dfs.journalnode.edits.dir</name>
            <value>/home/hadoop/workspace/journal</value>
      </property>
      <!-- 开启NameNode故障时自动切换 -->
      <property>
            <name>dfs.ha.automatic-failover.enabled</name>
            <value>true</value>
      </property>
      <!-- 配置失败自动切换实现方式 -->
      <property>
              <name>dfs.client.failover.proxy.provider.ns</name>
              <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
      </property>
      <!-- 配置隔离机制 -->
      <property>
               <name>dfs.ha.fencing.methods</name>
               <value>sshfence</value>
      </property>
      <!-- 使用隔离机制时需要ssh免登陆 -->
      <property>
              <name>dfs.ha.fencing.ssh.private-key-files</name>
              <value>/home/hadoop/.ssh/id_rsa</value>
      </property>
                                
      <property>    
          <name>dfs.namenode.name.dir</name>    
          <value>file:///home/hadoop/workspace/hdfs/name</value>    
      </property>    
      
      <property>    
          <name>dfs.datanode.data.dir</name>    
          <value>file:///home/hadoop/workspace/hdfs/data</value>    
      </property>    
      
      <property>    
         <name>dfs.replication</name>    
         <value>2</value>    
      </property>   
      <!-- 在NN和DN上开启WebHDFS (REST API)功能,不是必须 -->                                                                    
      <property>    
         <name>dfs.webhdfs.enabled</name>    
         <value>true</value>    
      </property>    
  </configuration>
  ```

* 文件 **mapred-site.xml**

* ```
  cp mapred-site.xml.template mapred-site.xml
  ```

* ```
  <configuration>
   <property>    
          <name>mapreduce.framework.name</name>    
          <value>yarn</value>    
   </property>    
  </configuration>
  ```

* 文件 **yarn-site.xml**：

* ```
  <configuration>
      <!-- 指定nodemanager启动时加载server的方式为shuffle server -->
      <property>    
              <name>yarn.nodemanager.aux-services</name>    
              <value>mapreduce_shuffle</value>    
       </property>  
       <!-- 指定resourcemanager地址 -->
       <property>
              <name>yarn.resourcemanager.hostname</name>
              <value>mast3</value>
        </property> 
  </configuration>
  ```

### 配置好后，在 Master 主机上，将 Hadoop 文件复制到各个节点上:

- master为主机名增加IP映射

- ```
  sudo vim /etc/hosts
  ```

- master分发Hadoop安装文件、脚本、公钥

- ```
  scp 文件名 目标文件名 
  ```

- slave输入sudo chmod +x ./**.sh命令，改变shell文件的运行权限

  ```
  sudo chmod +x ./**.sh
  ```

- slave运行add_ssh_key.sh （将master的公钥分发给slave）

- ```
  ./add_ssh_key.sh
  ```

- slave运行setup.sh 搭建运行环境

- ```
  ./setup.sh
  ```

* 最后在 Master 主机上就可以启动hadoop了:

* ```
  cd /usr/local/hadoop/
  bin/hdfs namenode -format
  sbin/start-dfs.sh
  sbin/start-yarn.sh
  jps             # 判断是否启动成功
  ```

* 若成功启动，则Master节点启动了`NameNode`、`SecondrryNameNode`、`ResourceManager`进程，Slave节点启动了`DataNode`和`NodeManager`进程。

### zookeeper集群的搭建

```
cat zoo.cfg 
```

 显示如下

```
# The number of milliseconds of each tick
tickTime=2000
# The number of ticks that the initial 
# synchronization phase can take
initLimit=10
# The number of ticks that can pass between 
# sending a request and getting an acknowledgement
syncLimit=5
# the directory where the snapshot is stored.
dataDir=/home/hadoop/zookeeper/data
dataLogDir=/home/hadoop/zookeeper/datalog
# the port at which the clients will connect
clientPort=2181
server.1=mast1:2888:3888  
server.2=mast2:2888:3888  
server.3=mast3:2888:3888 
```

### 集群的启动

###### 启动zookeeper集群

分别在master,master_standby上执行如下命令启动zookeeper集群；

```
sh zkServer.sh start
```

验证集群zookeeper集群是否启动，分别在master,master_standby上执行如下命令验证zookeeper集群是否启动，集群启动成功，有两个follower节点跟一个leader节点；

```
sh zkServer.sh status
JMX enabled by default
Using config: /home/hadoop/zookeeper/zookeeper-3.3.6/bin/../conf/zoo.cfg
Mode: follower
```

###### 启动journalnode集群

在master上执行如下命令完成JournalNode集群的启动

```
sbin/hadoop-daemons.sh start journalnode
```

执行jps命令，可以查看到JournalNode的java进程pid

###### 格式化zkfc,让在zookeeper中生成ha节点

在mast1上执行如下命令，完成格式化

```
hdfs zkfc –formatZK
```

格式成功后，查看zookeeper中可以看到

```
[zk: localhost:2181(CONNECTED) 1] ls /hadoop-ha
[ns]
```

###### 格式化hdfs

```
hadoop namenode –format
```

###### 启动NameNode

首先在master上启动active节点，在master上执行如下命令

```
sbin/hadoop-daemon.sh start namenode
```

在master_standby上同步namenode的数据

```
hdfs namenode –bootstrapStandby
sbin/hadoop-daemon.sh start namenode
```

###### 启动启动datanode

在master上执行如下命令

```
sbin/hadoop-daemons.sh start datanode
```

###### 启动year

```
sbin/start-yarn.sh 
```

###### 启动ZKFC

在master上执行如下命令，完成ZKFC的启动

```
sbin/hadoop-daemons.sh start zkfc
```

全部启动完后分别在master,master_standby上执行jps是可以看到下面这些进程的

```
#master上的java PID进程
[hadoop@Mast1 hadoop-2.5.2]$ jps
2837 NodeManager
3054 DFSZKFailoverController
4309 Jps
2692 DataNode
2173 QuorumPeerMain
2551 NameNode
2288 JournalNode
#master_standby上的java PID进程
[hadoop@Mast2 ~]$ jps
2869 DFSZKFailoverController
2353 DataNode
2235 JournalNode
4522 Jps
2713 NodeManager
2591 NameNode
2168 QuorumPeerMain

```

 




# 项目计划和设计文档

## 需求说明书

###

在一周内完成hadoop集群内的机器和角色分配矩阵，实现hadoop集群的搭建。分配十四台机器，十二台作为slaver，一台作为master， 一台作为standyby。

###计划说明

在一周内完成十四台机器的hadoop集群配置.
1.选定一台机器作为master;
2.在mster节点上配置hadoop用户、安装ssh server、安装java环境;
3.在其他slave节点上配置hadoop用户，然后通过master机器share配置环境脚本;
4.在其他slave节点上执行配置环境脚本，实现master连接slave节点;
5.实现standby节点的配置

总共搭了13台台式机的集群，其中一台作为master，一台作为standby，10台作为slaver，其中一台联网有故障，没有被考虑使用。

我们组四名成员花了一晚上时间试了单机版hadoop，金花同学在深入了解ssh及其相关问题。用了一个小时对每台机进行初始化，给每台机一个干净的环境，用一个晚上时间写了脚本，配了每台机的hadoop，

## 已有知识准备

戴神和金花用过hadoop，大家都学过Java，章鱼对Java有深入了解，大家都熟悉Linux的各种指令，大家都有Github。

## 需要做的事





## 安装脚本

```shell

```


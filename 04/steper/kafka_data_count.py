# coding: utf-8
from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils
 
file_path = "/test/v2"
sc=SparkContext("spark://116.56.136.57:7077","KafkaCount")

ssc=StreamingContext(sc,10)
zookeeper="116.56.136.57:2181"
#打开一个TCP socket 地址 和 端口号
topic={"dzg":1} #要列举出分区
groupid="test-consumer-group"

lines = KafkaUtils.createStream(ssc, zookeeper,groupid,topic)

data=lines.map(lambda x:x[1]) #注意 取tuple下的第二个即为接收到的kafka流


def map_split(x):
    try:
        date,ids,login = x.split(';')
        return (date[:-2]+'00',1 if login =='0' else 0)
    except BaseException:
        return ('0',0)


    
lines1 = data.map(map_split).reduce(lambda x,y:(x[0],x[1]+y[1])).map(lambda x:x[0]+';'+str(x[1]))


lines1.saveAsTextFiles(file_path)


lines1.pprint()
#启动spark streaming应用
ssc.start()

#等待计算终止
ssc.awaitTermination()


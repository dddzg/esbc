from pyspark import SparkContext,SparkConf
from datetime import datetime

def setConfig(masterPath,appName):
    conf = SparkConf()
    conf.setMaster(masterPath)
    conf.setAppName(appName)
    return conf

def map_split(x):
    date,ids,login = x.split(';')
    return (ids,(datetime.strptime(date, "%Y-%m-%d %H:%M:%S"),login))

def reducer(x,y):
    return (x[0]+y[0],x[1]+y[1])

def sortByKey(x):
    try:
        datas = sorted(x,key=lambda sample:sample[0])
        all_sec,count = 0,0
        for i in xrange(0,len(datas),2):
            all_sec += (datas[i+1][0]-datas[i][0]).total_seconds()
            count += 1
        if count<=0:
            return (0,0)
        return (all_sec,count)
    except BaseException:
        return (0,0)

masterPath = "spark://116.56.136.57:7077"
appName = "spark application"
path = 'hdfs://master:9000/random/logs.dat'

conf = setConfig(masterPath,appName)
sc = SparkContext(conf=conf)
textFile = sc.textFile(path)
all_time,count = textFile.map(map_split).groupByKey().mapValues(sortByKey).map(lambda x:x[1]).reduce(reducer)

print(all_time/count if count!=0 else 0)

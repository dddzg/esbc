# 找到spark streaming的处理上限
> 测试环境：topic只设置了一个partition。
* 首先让程序正常运行，启动kafka生产者和消费者，并运行实时向kafka中写数据的python程序。
* 打开Spark控制台，查看Spark程序运行细节。（master:8080/）
* 选择Streaming那个Tab，在这个Tab下的内容包括了输入比率的流统计图，它显示了每秒的事件数量，以及处理所花费的毫秒数。
* 不断加大每秒向kafka中写数据的数据量，并观察Spark控制台的图形输出。
* 主要观察Streaming Statistics下的第一个图，也就是Input Rate，折线图展示了streaming每秒处理的事件数量。

当每秒向kafka中写1000条数据时，Input Rate显示streaming每秒处理的数据量在1000左右。
当将每秒写入kafka的数据增加到10000条时，Input Rate显示的streaming每秒处理的数据量和每秒写入数据量仍保持一致。
但当每秒写入kafka的数据增加到100000条时，发现Input Rate显示的streaming每秒处理的数据量仍为10000条。
这表明，streaming在当前配置下，每秒可处理数据上限为10000条。

并且，如果没有达到streaming的处理上限，在Input Rate那张处理速率的图中，折线图是会有周期性峰值的，一旦达到streaming的处理
上限，折线图基本上保持一条直线。

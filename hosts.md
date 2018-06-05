机器分布图

slave_04|slave_03 | slave_02 |  |slave_01| slave_00 |master_standby |master
------------ | ------------ | ------------- | ------------|------------ | ------------- | ------------|------
slave_11|slave_10 | slave_09 |  |slave_08| slave_07 |slave_06 |slave_05

ip-hostname 对应表

ip | hostname
------------ | ------------
 ..  |master
  .. |master_standby
 ..  |slave_00
 116.56.136.61 | slave_02
116.56.136.64  |slave_05
116.56.136.65 | slave_06
116.56.136.68 | slave_09

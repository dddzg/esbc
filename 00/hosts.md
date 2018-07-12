机器分布图


slave_04|slave_03 | slave_02 |  |slave_01| slave_00 |master_standby |master
------------ | ------------ | ------------- | ------------|------------ | ------------- | ------------|------
<b>slave_11</b>|*slave_10* | *slave_09* |  |*slave_08*| *slave_07* |*slave_06* |*slave_05*

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



sudo gedit /etc/hosts

116.56.136.57	  master

116.56.136.58   master-standby

116.56.136.59   slave_00

116.56.136.60   slave_01

116.56.136.61   slave_02

116.56.136.62   slave_03

116.56.136.63   slave_04

116.56.136.64   slave_05

116.56.136.65   slave_06

116.56.136.66   slave_07

116.56.136.67   slave_08

116.56.136.68   slave_09

116.56.136.69   slave_10

116.56.136.70   slave_11

sudo gedit /etc/hostname

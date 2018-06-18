#!/bin/bash
#install java
apt-get install default-jdk
echo export JAVA_HOME=/usr/lib/jvm/default-java >> ~/.bashrc
source ~/.bashrc

#install hadoop
#sudo rm -r /usr/local/hadoop
sudo tar -zxf ~/hadoop.master.tar.gz -C /usr/local
sudo chown -R hadoop /usr/local/hadoop
#tar -zxf ~/hadoop-2.6.0-cdh5.13.3.tar.gz -C /usr/local
#mv /usr/local/hadoop-2.6.0-cdh5.13.3/ /usr/local/hadoop
#chown -R hadoop /usr/local/hadoop/
#sed -i "s/\${JAVA_HOME}/usr\/lib\/jvm\/default-java/g" /usr/local/hadoop/etc/hadoop/hadoop-env.sh





##下载zookeeper(版本zookeeper-3.4.14)
#####zookeeper官网下载链接地址
#####将下载得到的压缩包解压到对应的目录下
#####进入conf文件夹，修改zookeeper的配置
#####将zoo_sample.cfg重命名为zoo.cfg；
#####修改zoo.cfg，修改的地方为下方配置文件的中文说明
#### 修改dataDir配置，该目录为zookeeper保存数据的目录
#####dataDir=E:\\zookeeper\\data
#### 新增dataLogDir配置，该目录为zookeeper保存日志文件的目录
#####dataLogDir=E:\\zookeeper\\log
#####进入zookeeper\bin文件夹，点击zkServer.cmd，不关闭当前窗口;
#####cmd出新的窗口，输入netstat -na,查看服务是否启动成功；

##下载redis(版本Redis-x64-3.2.100)
#####进入redis\bin文件夹
#####执行命令: redis-server.exe redis.windows.conf

##下载kafka(版本kafka_2.13-2.5.0)
#####kafka自带了zookeeper的, 所以启动kafka, 需要先启动自带的zookeeper
#####进入kafka的目录kafka_2.13-2.5.0
#####执行命令: .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
#####然后再开个cmd窗口, 再进入kafka_2.13-2.5.0目录
#####在执行命令: .\bin\windows\kafka-server-start.bat .\config\server.properties

##maven 操作
#####Oracle的ojdbc.jar是收费的, 所以maven的中央仓库中没有这个资源，只能通过配置本地库才能加载到项目中去
#####oracle的ojdbc6.jar是收费的, 所以使用下面方式安装jar包, 把下载的ojdbc6包放在一个目录(下面是我放的目录)
#####执行命令: mvn install:install-file -Dfile=E:\\idealProject\\Demo\\lib\\ojdbc6-11.2.0.3.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.3 -Dpackaging=jar
#####出现问题: Error creating bean with name 'serverEndpointExporter' defined in class path
#####解决办法: 加-DskipTests, 例如mvn package -DskipTests, mvn install -DskipTests, ...

## Install Rabbitmq-server
#####fisrt install Rabbitmq-server, please follow url install.  
#####(first install erLang, then install Rabbitmq-server)   
#####https://www.cnblogs.com/xumBlog/p/10622390.html  
#####use System or Administrator rights open cmd, then input follow command:  
#####cd (Rabbitmq_install_path)\sbin  
#####start Rabbitmq-server command: rabbitmq-server start  
#####restart Rabbitmq-server command: rabbitmq-server restart  
#####stop Rabbitmq-server command: rabbitmq-server stop or ctrl + c  
#####Enabling maintenance plug-in command: rabbitmq-plugins enable   rabbitmq_management
#####login: http://localhost:15672/  
#####username: guest  
#####password: guest
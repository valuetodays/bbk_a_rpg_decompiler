# bbk_a_rpg_decompiler

#### 徽章

[![Sonar Quality Gate](http://192.168.0.204:9000/api/badges/measure?key=com.billy.bbk.arpg%3Abbk_a_rpg_decompiler&metric=alert_status&t=1233)](http://192.168.0.204:9000/dashboard?id=com.billy.bbk.arpg%3Abbk_a_rpg_decompiler)
[![Sonar Coverage](http://192.168.0.204:9000/api/badges/measure?key=com.billy.bbk.arpg%3Abbk_a_rpg_decompiler&metric=coverage&t=1233)](http://192.168.0.204:9000/dashboard?id=com.billy.bbk.arpg%3Abbk_a_rpg_decompiler)

#### 介绍
步步高a系列rpg反编译，本项目基于/docs下的文档修改而成，完全是个人爱好。
目前官方的LIB包里面有SUN段，暂不知道它的用途。

#### 打包命令

 mvn clean package -DskipTests=false
 
 当有sonar服务时可以执行如下命令
 
 mvn clean test sonar:sonar -Dsonar.host.url=http://sonar-ip:port -DskipTests=false
 
 如我的配置
 
 mvn clean test sonar:sonar -Dsonar.host.url=http://s204:9000 -DskipTests=false

 #### 打包测试类成jar并运行

`mvn clean package -DskipTests` and run `run-test.[sh/bat]`

# 基于apache activemq 实现 mqtt 鉴权

一个设备一个账号（设备ID作为用户名，密码随机字符串）

所有主题以 "iot/用户ID/设备ID/" 开头

同一个用户的设备，可以相互访问

不同用户设备不能相互订阅和发布主题


把代码打一个jar出来
这里使用的是 apache activemq 5.15.9 版本
把打包出来的jar和 源代码中的lib里面的jmysql-connector-java-5.1.30.jar，spring-jdbc-4.3.18.RELEASE.jar包都扔到apachemq的lib下
把 activemq.xml db.properties 扔到 conf下

db.properties 里面配置数据库

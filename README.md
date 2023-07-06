## mybatis-plus-generator-ui
 提供交互式的Web UI用于生成兼容mybatis-plus框架的相关功能代码，包括Entity,Mapper,Mapper.xml,Service,Controller等
 ，可以自定义模板以及各类输出参数，也可通过SQL查询语句直接生成代码。
 
## 使用方法

1.  使用方法

```xml
java -jar \
-DserverPort=8088 \
-DdbUrl=localhost:3306 \
-DdbName=db_e_commerce_center \
-DdbUserName=root \
-DdbPassword=root \
-DbasePackage=com.wm.mall.settings \
-DnameBeginIndex=1 \
wm-generator-web-1.0.0.jar
```

参数解释：
```xml
serverPort: 服务启动端口
dbUrl: 数据库ip + 端口
dbName: 数据库实例名称
dbUserName: 数据库用户名
dbPassword: 数据库密码
basePackage: 生成包路径
nameBeginIndex: 去除名字起始位置
```

2.访问：http://localhost:8088 

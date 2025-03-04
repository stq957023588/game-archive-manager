# 游戏存档管理服务器

用于管理游戏存档

请搭配[本地游戏存档管理器](https://github.com/stq957023588/LocalGameArchiveManager)一起使用

# 安装启动

 1. 安装JDK

 2. 获取可执行Jar

    * 从Release下载
    * 下载源码，在根目录执行``gradlew bootJar``

 3. 将Jar放在一个文件夹内，并在文件夹内创建一个.env文件

    ```.env
    # 缓存类型，可选：memory,redis
    CACHE_TYPE: redis
    # 消息队列类型，可选：memory,rabbit
    MESSAGE_QUEUE_TYPE: rabbit
    # 数据库类型，可选：sqlite,mysql
    STORE_TYPE: sqlite
    # 服务器开放端口
    SERVER_PORT: 9001
    ```

 4. 执行命令

    ```shell
    java -jar app.jar
    ```

    
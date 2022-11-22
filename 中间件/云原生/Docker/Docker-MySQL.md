# MySQL安装

## 1. Docker Hub上面查找mysql镜像

```
sudo docker search mysql
```

![](https://raw.githubusercontent.com/bigshcool/myPic/main/20221121155014.png)

## 2.  拉取mysql:5.7版本

```
sudo docker pull mysql:5.7
```

## 3. 运行容器

```
sudo docker run -d -p 3306:3306 --privileged=true -v /cugb/mysql/log:/var/log/mysql -v /cugb/mysql/data:/var/lib/mysql -v /cugb/mysql/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=123456  --name mymysql mysql:5.7
```

![image-20221121155553318](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20221121155553318.png)

## 4.修改my.cnf使其支持中文

```shell
cd /cugb/mysql/conf
sudo vim my.cnf

# 输入以下内容
[client]
default_character_set=utf8
[mysqld]
collation_server = utf8_general_ci
character_set_server = utf8
```

![image-20221121155936801](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20221121155936801.png)

## 5.重启容器

```sh
sudo docker restart mymysql
```

![image-20221121160252563](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20221121160252563.png)

## 6. 进入容器

```shell
# 进入容器
docker exec -it mymysql bash

# 登录mysql
mysql -uroot -p
```

## 7.修改密码

```mysql
# 为了安全起见，修改一下密码
set password for root@localhost = password("cugb123456");
# 后期发现只输入上面命令会出现问题，需要补充下面命令，才能正确修改让JAVA连接
set password for 'root'@'%'=password('12345678');
flush privileges;
```

![image-20221121161250486](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20221121161250486.png)



## 8. 退出Mysql应用（退出应用后，还在容器中）

![image-20221121161442003](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20221121161442003.png)

## 9. 退出容器

```
# 按下ctrl + P + Q 
```

![image-20221121161656976](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20221121161656976.png)

查询下发现mymysql依然还在运行。

## 10.MySQL的properties

```properties
jdbc.driverclass=com.mysql.jdbc.Driver
jdbc.url=jdbc\:mysql\://ip:3306/user_db?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai
jdbc.username=root
jdbc.password=密码
```




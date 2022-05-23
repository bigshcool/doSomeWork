# Docker

**Docker是解决运行环境和配置问题的软件容器，方便做持续集成并且有助于整体发布的容器虚拟化技术**

## 1. 虚拟机和容器比较

- 虚拟机

  它可以在一种操作系统里面运行的另一种操作系统，比如在Windows10系统里面运行Linux系统CentOS7.应用程序对此毫无感知，因为虚拟机看起来就跟真实系统一模一样，而对于底层系统来说，虚拟机就是普通文件，不需要就删掉，对其它部分毫无影响。这类虚拟机完美的运行了另外一套系统，能够使应用程序、操作系统和硬件的逻辑不变。

  传统虚拟机技术基于安装在主操作系统上的虚拟机管理系统如VM，然后创建虚拟机，虚拟出各种硬件，在虚拟机上安装操作系统，在操作系统中安装部署各种应用。

  - 缺点
    - 资源占用多
    - 冗余步骤多
    - 启动慢

- 容器

  Linux容器时与系统其他部分隔离开的一系列进程，从另一个镜像运行，并由该镜像提供支持进程所需的全部文件。容器提供的镜像包含了应用的所有依赖项，因而在从开发到测试再到生产的整个过程中，他都具有可移植性和一致性。

  Linux容器不是模拟一个完章的操作系统而是对进程进行隔离。有了容器，就可以将软件运行所需的所有资源打包到一个隔离的容器中。容器与虚拟机不同，不需要捆绑一整套操作系统，只需要软件工作所有的库资源和设置。系统因此而变得高校并且保证部署在任何环境中的软件能够始终如一的运行。

**Docker容器是在操作系统层面实现虚拟化，直接复用本地主机的操作系统，而传统虚拟机则是在硬件层面实现虚拟化。与传统的虚拟机想比，Docker优势体现为启动速度快，占用体积小，同时每个容器之间相互隔离，每个容器都有自己的文件系统，容器之间进程不会相互影响，能区分计算资源。**

- **Docker会比VM快的原因**

  - Docker有着比虚拟机更少的抽象层

    由于docker不需要像虚拟机一样实现硬件资源的虚拟化，运行在docker容器上的程序直接使用的都是实际的硬件资源，因此在CPU、内存利用率上docker将会在效率上有明显的提升。

  - Docker利用的时宿主机的内核，而不需要加载操作系统的内核。

    当新键一个容器时，docker不需要和虚拟机一样重新加载一个操作系统内核。进而避免寻址、加载操作系统内核返回等比较费时间费资源的过程，当新键一个虚拟机时，虚拟机软件需要加载OS，返回新建的过程是分钟级的。而Docker由于直接利用宿主机的操作系统，则省略了返回过程，因此新键一个docker容器只需要几秒钟。

## 2. Docker安装

Docker官网：http://www.docker.com

Docker Hub官网(安装Docker镜像)：https://hub.docker.com/

### 2.1 安装须知

Docker并非是一个通用的容器工具，它依赖于已经存在并且运行的Linux内核环境。

Docker实质上是在已经运行的Linux下制造了一个隔离的文件环境，因此它执行效率几乎等同于所部署的Linux主机。

因此，**Docker必须部署在Linux内核上，如果其他系统想要部署Dokcier就必须安装一个Linux环境**

目前CentOS仅仅发行版本中的内核支持Docker。Docker运行在CeontOS7（64-bit）上，

要求系统为64位，Linux系统内核版本位3.8以上。

```
查看内核
cat /etc/redhat -release
```

### 2.2 Docker的基本组成

- 镜像image

  Docker镜像相当于一个只读模板。镜像可以用来创建Docker容器，一个镜像可以创建很多容器。

  他也相当于是一个root文件系统。比如官方镜像centos7，就包含了完整的以套centos7最小系统的root文件系统。

  相当于容器的"源代码"，dockers镜像文件类似于Java的类模板，而Docker容器实例类似于java中new出来的实例对象。

- 容器container

  Docker利用容器(container)独立运行的一个或者一组应用，应用程序或服务运行在容器里面，容器就类似于一个虚拟化的运行环境，容器是用镜像创建的运行实例。就像是JAVA中的类和实例对象一样，镜像是静态的定义，容器时镜像运行时的实体。容器位镜像提供了一个标准和隔离的运行环境。它可以被启动、开始、停止、删除。每个容器都是相互隔离的，保证安全的平台。

  可以将容器看作一个简易版的Linux环境(包括root用户权限，进程空间、用户空间和网络空间和运行在其中的应用程序)

- 仓库repository

  是集中存放镜像文件的场所。

  类似于

  Maven仓库，存放各种jar包的地方；

  github仓库，存放各种git项目的地方；

  Docker公司提供的官方registry被称为Docker Hub，存放各种镜像模板的地方。

  仓库分为公开仓库(Public)和私有仓库(Private)两种形式

  最大的公开仓库是Docker Hub(https://hub.docker.com)，存放了数量庞大的镜像提供用户下载。

### 2.3 Docker基本流程

**Docker是一个C/S模式的架构，后端是一个松耦合，众多模块各司其职**

1. 用户是使用Docker Client和Docker Daemon建立通信，并发送请求给后端
2. Docker Daemon作为Docker架构中的主体部分，首先提供Docker Server的功能使它可以接受Docker Client的请求
3. Docker Engine作为执行Docker内部的一系列工作，每一项工作都是以一个Job的形式存在的。
4. Job运行过程中，当需要容器镜像时，则从Docker Registry下载镜像，并通过镜像管理驱动Graph driver将下载镜像以Graph形式存储。
5. 当需要为Docker创建网络环境时，通过网络管理驱动Network driver创建并且配置Docker容器网络环境
6. 当需要限制Docker容器运行资源或执行用户指令操作时，则需要通过Exec driver来完成
7. Libcontainer是一项独立的容器管理包，Network driver以及Exec driver都是通过Libcontainer来实现具体对容器进行的操作。

### 2.4 安装步骤

1. 首先安装gcc环境

```shell
yum -y install gcc
yum -y install gcc-c++
yum install -y yum-utils
```

2. [Centos7以上版本安装方法官网]([Install Docker Engine on CentOS | Docker Documentation](https://docs.docker.com/engine/install/centos/))

```shell
注意:在安装stable repository时需要将网址换成国内的镜像，不然老会爆出超时的错误
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

然后更新一下yum软件的包的索引,将软件包信息提前在本地缓存一份，用来提高搜索安装软件的速度
yum makecache fast
```

3. 镜像加速器

   - 首先需要注册阿里云（地址：https://www.aliyun.com/）账号，已有账号的登录后，在阿里云控制台找到容器镜像服务。

     ![阿里云镜像加速](https://cdn.jsdelivr.net/gh/bigshcool/myPic@main/阿里云镜像加速.5n73qvs7mjo0.jpg)

   - 按照操作文档进行操作

     ![镜像加速操作文档](https://cdn.jsdelivr.net/gh/bigshcool/myPic@main/镜像加速操作文档.2u0mi7vp9dg0.jpg)


## 3. Docker常用命令

### 3.1 帮助启动类命令

**启动Docker**

```shell
systemctl start docker
```

**停止Docker**

```shell
systemctl stop docker
```

**重启Docker**

```shell
systemctl restart docker
```

**查看Docker状态**

```shell
systemctl status docker
```

**开机启动**

```shell
systemctl enable docker
```

**查看Docker信息**

```shell
docker info
```

**查看Docker总体帮助文档**

```shell
docker--help
```

**查看docker命令帮助文档**

```shell
docker 具体命令 --help
```

### 3.2 镜像命令

**罗列本地存有的所有镜像**

```
docker images

OPTIONS说明
-a 列出本地所有镜像（含历史镜像）
-q 只显示镜像ID
```

![image](https://cdn.jsdelivr.net/gh/bigshcool/myPic@main/image.5za7slj9ze40.jpg)

REPOSITORY:表示镜像的仓库源

TAG:镜像标签版本号

IMAGE_ID:镜像ID

CREATED:镜像创建时间

SIZE:镜像大小

同一仓库源可以有多个版本号，代表仓库源的不同版本，我们使用REPOSITORY:TAG来定义不同的镜像。

如果你不指定一个镜像的版本标签，例如你只是用ubuntu，docker将默认使用ubuntu:latest镜像。



**查询镜像**

```shell
Docker search 镜像名

OPTIONS说明
--limit 5 只列出5个 不填写数字 默认25个
```



**下载某个镜像**

```dockerfile
docker pull 镜像名[:TAG]
没有TAG的话就是表示最新的版本的镜像
```



**查看镜像/容器/数据卷所占用的空间**

```
docker system df 
```



**删除镜像**

```
docker rmi IMAGES_ID(镜像ID)

OPTIONS说明
-f 强制删除
```



- **虚悬镜像是什么？**

  指的是**仓库名、标签**都是none的镜像，所称虚悬镜像，建议删除

### 3.3 容器命令

**新键启动容器**

```
docker run [OPTIONS] IMAGE [COMMAND][ARG...]

OPTIONS说明
--name="容器新名字"   为容器指定一个名称
-d:后台运行容器并且返回容器ID,也即启动守护式容器(后台运行)

-i:以交互模式运行容器，通常与-t同时使用
-t:为容器重新分配一个伪数据终端，通常与-i同时使用

-P:随机端口映射，大写P
-p:指定端口映射，小写p

退出终端直接在伪终端中输入exit
```

**列出所有正在运行的容器**

```
docker ps [OPTIONS]

OPTIONS说明
-a:列出当前所有正在运行的容器+历史上运行过的
-l:显示最近创建的容器
-n:显示最近n个创建的容器
-q:静默模式,只显示容器编号
```

**容器退出方式**

- 容器停止

```
在容器伪终端中输入exit
```

- 容器不停止

```
ctrl+p+q
```

**启动已经停止的容器**

```
docker start 容器ID或者容器名
```

**重启容器**

```
docker restart 容器ID或者容器名
```

**停止容器**

```
docker stop 容器ID或者容器名
```

**强制停止容器**

```
docker kill 容器ID或者容器名
```

**删除已停止的容器**

```
docker rm [OPTIONS] 容器ID

OPTIONS:
-f 强制删除没有停止的
```






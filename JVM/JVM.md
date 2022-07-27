# JVM

## 1.JVM整体架构

![](https://raw.githubusercontent.com/bigshcool/myPic/main/202207211026047_JVM_%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84.png)

- HotSpot VM是目前市面上高性能虚拟机的代表作之一
- 它采用解释器与即时编译器并存的架构
- 在今天，Java程序的运行性能早已经脱胎换骨，已经达到了可以和C/C++一较高下的地步。
- **在运行时数据区中的堆和方法区是多线程共享的，而栈、本地方法区、程序计数器是每个线程独享的**

## 2.Java代码执行流程

![](https://raw.githubusercontent.com/bigshcool/myPic/main/202207211128873_java_%E4%BB%A3%E7%A0%81%E6%B5%81%E7%A8%8B%E5%9B%BE.png)

- 翻译字节码和编译执行的操作都在**执行引擎**中处理，JIT会将经常执行的热点代码二次编译成机器码，加快程序的运行速度。

## 3. JAVA是基于栈的指令集架构

指令集存在两种架构方式：**1.基于栈的指令集架构 2.基于寄存器的指令集架构，两种架构的指令集之间的区别:**

- 基于栈式架构的特点：
  - 设计与实现更简单，适用于资源受限的系统
  - 避开了寄存器的分配难题，使用零地址指令方式分配
  - 指令流中的指令大部分是零地址指令，其执行过程依赖于操作栈，**指令集更小**，编译器容易实现
  - 不需要硬件支持，可移植性更好，更好实现跨平台
- 基于寄存器架构的特点：
  - 经典的应用是x86的二进制指令集，比如传统的PC以及Android的Davlik虚拟机
  - 指令集架构则完全依赖硬件，可移植性差
  - 性能优秀和执行更高效
  - **花费更少的指令去完成一项操作**。
  - 大部分情况下，基于寄存器架构的指令集往往都是以一地址指令、二地址指令和三地址指令为主，而基于栈式架构的指令集却是以零地址指令为主。

**补充：完成一项操作需要多条指令，而每条指令的长度栈式架构的更短，因为他是基于栈这种数据结构，只对栈顶元素进行操作即可，但是栈式架构要比寄存器架构使用的指令条数更多**

```
//当你在终端对字节码文件输入javap 字节码文件名 即可完成对字节码的反编译
 public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=4, args_size=1
         0: iconst_2
         1: istore_1
         2: iconst_3
         3: istore_2
         4: iload_1
         5: iload_2
         6: iadd
         7: istore_3
         8: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
        11: iload_3
        12: invokevirtual #3                  // Method java/io/PrintStream.println:(I)V
        15: return
```

​	**之所以Java要基于栈的指令集来完成操作，是因为不同的CPU可能采用不同的寄存器架构，那么就无法做到跨平台**

## 4. JVM的生命周期

- 虚拟机的启动

  Java虚拟机的启动是经过引导类加载器(Bootstrap class loader)创建一个初始类(initial class)来完成，这个类是有虚拟机的具体实现指定的。

- 虚拟机的执行

  - 一个运行中的Java虚拟机有者一个清晰的任务，执行Java程序
  - 程序开始执行他才运行，程序结束时他就停止
  - **执行一个所谓的JAVA程序的时候，真正在执行的是一个叫作Java虚拟机的进程**

- 虚拟机得退出

  - 程序正常执行结束
  - 程序在执行过程中遇到了**异常或错误**而异常终止
  - 由于操作系统出现错误而导致Java虚拟机进程终止
  - 某个线程效用Runtime类或者System的exit方法，或者Runtime类的halt方法，并且JAVA安全管理器也允许这次exit或者halt操作
  - 除此之外，JNI（JAVA Native Interface）规范描述了用JNI Invocation API来加载或者卸载 JAVA虚拟机时，Java虚拟机的推出情况

## 5. JVM发展历程

- Sun Classic VM

  - 在1996年JAVA1.0时，Sun公司发布了一款名为Sun Classic VM的JAVA虚拟机，他同时也是世界上第一款商用JAVA虚拟机，JDK1.4时完全被淘汰

  - 这款虚拟机内部只提供了解释器
  - 如果使用JITB编译器，就需要外挂，但是一旦使用JIT编译器，JIT就会接管虚拟机的执行系统。解释器就不再工作。解释器和编译器不能配合工作
  - 现在Hotspot内置了此虚拟机
- Exact VM

  - 为了解决上一个虚拟机问题，JDK1.2时，sun提供了此虚拟机
  - Exact Memory Management:准确式内存管理
    - 也可以叫Non-Conservative/Accurate Memory Management
    - 虚拟机可以知道内存中某个位置的数据类型是什么类型
  - 具备现代高性能虚拟机的雏形
    - 热点检测
    - 编译器和解释器混合工作模式
  - 只在Solaris平台短暂使用，其他平台上还是classic vm
    - 英雄气短，最终被Hotspot虚拟机替换
- HotSpot VM

  - 不管是现在仍在广泛使用JDK6,还是使用比例较多的JDK8中，默认的虚拟机都是HotSpot
  - Sun Orcle JDK 和 Open JDK默认虚拟机
  - 从服务器、桌面到移动端、嵌入式都有应用
  - HotSpot指的就是他的热点代码探测技术
    - 通过计数器找到最具编译价值代码、触发即时编译或者栈上替换
    - 通过编译器与解释器协同工作，在最优化的程序响应时间与最佳执行性能中取得平衡。
- JRockit

  - 专注于服务器端

  - 它可以不太关注程序的启动速度，因此JRockit内部是不包含解析器实现，全部代码都靠即时编译器编译后执行
  - 大量的行业基准测试显示，JRockit JVM是世界上最快的JVM
    - 使用JRockit产品，客户已经体验了显著的性能提升，提携超过了70%和硬件成本的减少(达50%)
  - 优势：全面的java运行时解决方案组合
    - JRockit面向延迟敏感型应用的解决方案JRockit Real Time提供以毫秒或毫秒级JVM响应时间、适合财务、军事指挥、电信网络的需要
    - MissionControl服务套件，它是一组以极低的开销来监控、管理和分析生产环境中的应用程序的工具

- J9
  - 市场定位与HotSpot接近，服务器端、桌面应用、嵌入式等多用途VM
  - 广泛使用IBM的各种JAVA产品
  - 有影响力的三大商用虚拟机之一，在IBM产品上号称应用使最快的。

## 6.类加载器子系统

![](https://raw.githubusercontent.com/bigshcool/myPic/main/202207261129826.png)

- 类加载器子系统负责从文件系统或者网络中加载Class文件，class文件在文件开头有特定的文件标识
- ClassLoader只负责class文件的加载，至于他是否可以运行，则由Execution Engine决定
- 加载的类信息存放于以快成为方法区的内存空间。除了类的信息外，方法去还会存放运行时常量池信息，可能还包括字符串字面量和数字常量

### 6.1 类加载器ClassLoader角色

​		 ![](https://raw.githubusercontent.com/bigshcool/myPic/main/202207261139382.png)

- Class File存储在本地硬盘上，可以理解为模板，而最终需要根据这个模板在执行的时候实要加载到JVM当中来，根据这个文件实例化n个一模一样的实例。
- Class File 加载到JVM中，被称为DNA元数据模板，放在方法qu
- 在.class文件->JVM->最终成为元数据模块，此过程就要一个运输工具（类装载器 Class Loader），扮演一个快递员的角色
- Hello类的加载过程
















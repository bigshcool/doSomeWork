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


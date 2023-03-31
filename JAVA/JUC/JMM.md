# JMM（JAVA内存模型）

- 什么是JMM

  在JVM规范中试图定义一种Java内存模型(简称 JMM)来**屏蔽各种硬件和操作系统的内存访问差异**。以实现让Java程序在各种平台下能达到的一致性内存访问效果。

  JMM本身是一种抽象的概念并不真实的存在它仅仅是描述的是一组约定或者规范，通过这组规范定义了程序中（尤其是多线程）各个变量的读写方式并决定一个线程对共享变量的写入何时以及如何变成另一个线程可见，关键技术点都是围绕多线程的**原子性、可见性和有序性。**JMM的关键技术点都是围绕多线程的**原子性、可见性和有序性展开的。**

  - 能干嘛
    - 通过JMM来实现线程和主内存之间的抽象关系
    - 屏蔽各个硬件平台和操作系统的内存访问差异以实现让JAVA程序在各种平台下都能达到一致的内存访问效果。

- JMM三大特性

  - 可见性

  ![image-20230327163211222](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230327163211222.png)

  ​		**是指当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道该变更**，JMM指定了所有的变量都存储到主内存中。系统主内存共享变量数据修改被写入的时候是不确定的，多线程并发下很可能出现“脏读”，**所以每个线程都有自己的工作内存**，线程自己的工作内存中保存了该线程使用到的变量的主内存副本拷贝，线程对变量的所有操作(读取，赋值等)都必需在线程自己的工作内存中进行，而不能够直接读写内存中的变量。不同线程之间也无法直接访问对方工作内存中的变量，线程间变量值的传递均需要通过主内存完成。

  | 线程脏读                                                     |
  | ------------------------------------------------------------ |
  | 主内存中变量x,初始值为0                                      |
  | 线程A要将x加1，先将x=0拷贝到自己的私有内存中，然后更新x的值  |
  | 线程A将更新后的x值回刷主内存的时间不是固定的                 |
  | 刚好在线程A没有回刷x到内存时，线程B同样从主内存读取x，此时为0，线程A一样的操作，最后期盼的x = 2就会变成x = 1 |

  - 原子性

    指一个操作是不可打断的，即多线程线程中，操作不能被其他线程干扰。

  - 有序性

    对于 一个线程的执行代码，我们总习惯性认为代码的执行总是从上到下，有序执行。但是为了提升性能，编译器和处理器通常会指令通常会指令进行重拍。JAVA规范规定JVM线程内部维持顺序化语义，即只要程序的最终结果与它顺序执行的结果灯箱，那么指定的顺序可以与代码顺序不一致，此过程叫做指令的重排序。

    指令重拍可以保证串行语义一致，但是没有义务保证多线程的语义一致，简单说。

    两行以上不相干的代码在执行的时候有可能执行的不是第一条，**不见得从上到下顺序执行，执行顺序会被优化。**

    从源码到最终执行示例图:

    ![image-20230327174017228](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230327174017228.png)

    单线程环境里面确保程序最终执行的结果和代码顺序执行的结果一致。
    
    处理器在进行重排序的时候必须要考虑指令的**数据依赖性**
    
    多线程环境中由于线程中的交替执行，由于编译器优化重拍的存在，两个线程中的变量能否保证一致性是无法确定的，结果无法预测的。



## 1.多线程先行发生原则之happens-before

在JMM中，如果一个**操作执行的结果**需要对另外一个操作可见性或者代码重排序，那么这两个操作之间必须存在happens-before(先行发生）原则。逻辑上的先后关系。



### 1.1 案例

| x = 5  | 线程A执行 |
| ------ | --------- |
| y = x  | 线程B执行 |
| 写后读 |           |

y可能不等于5，如果线程A的操作(x=5)happens-before（先行发生）线程B的操作（y = x），那么可以确定线程B执行后y = 5一定成立。

如果他们不存在happens-before原则，那么y = 5不一定成立。

**happens-before：保证了可见性和有序性**



如果Java内存模型中所有的有序性都是仅仅依靠volatile和synchronized来完成，那么又很多操作都将会变得非常啰嗦。

但是我们在编写JAVA并发代码的时候并没有察觉到这一点，我们没有时时，处处，次次添加volatile和synchronized来完成程序，这就是java语言中的JMM原则下有一个“先行发生”（Happens-Before）的原则限制和硅谷，给你立好了规矩。



### 1.2 happens-before总原则

- 如果一个操作先行发生于另外一个操作，那么第一个操作的执行结果将对第二个操作可见，并且第一个操作的执行顺序将会在第二操作之前。
- 两个操作间存在happens-before关系，并不一定以为要按照happens-before原则限定的顺序来执行，**如果重排序以后对结果没有任何影响，那么这种重排序并不非法**



### 1.3 happens-before官网八条

- 次序规则
  一个线程内,按照代码顺序,写在前面的操作先行发生于写在后面的操作(强调的是一个线程)
  前一个操作的结果可以被后续的操作获取。将白点就是前面一个操作把变量X赋值为1,那后面一个操作肯定能知道X已经变成了1
-  锁定规则
  (一个unlock操作先行发生于后面((这里的"后面"是指时间上的先后))对同一个锁的lock操作(上一个线程unlock了,下一个线程才能获取到锁,进行lock))
- volatile变量规则
  (对一个volatile变量的写操作先行发生于后面对这个变量的读操作,前面的写对后面的读是可见的,这里的"后面"同样是指时间是的先后)
- 传递规则
  (如果操作A先行发生于操作B,而操作B又先行发生于操作C,则可以得出A先行发生于操作C)
- 线程启动规则(Thread Start Rule)
  (Thread对象的start( )方法先行发生于线程的每一个动作)
- 线程中断规则(Thread Interruption Rule)

对线程interrupt( )方法的调用先发生于被中断线程的代码检测到中断事件的发生。可以通过Thread.interrupted( )检测到是否发生中断

- 线程终止规则(Thread Termination Rule)
  线程中的所有操作都先行发生于对此线程的终止检测，可以通过isAlive方法是否先行发生线程的每一个动作。
- 对象终结规则(Finalizer Rule)
  (对象没有完成初始化之前,是不能调用finalized( )方法的 )

### 1.4 总结

在java语言中，Happens-Before的语义本质上是一种可见性。A Happens-Before B 意味着A发生过的事情对B来说是可见的，无论事件A和B事件是否发生在同一个线程里。



JMM的设计分为两个部分:

一部分面向我们程序员提供的，也就是happens-before规则，它通俗易懂的向我们程序员阐述了一个强内存模型，我们只要理解happens-before规则，就可以编写并发安全的程序了。

另一部分是针对JVM实现的，为了尽可能少的对编译器和处理器做约束而提高性能，JMM在不影响程序执行的结果的前提下对其不做要求，即允许优化重排序。物品们只需要关注前者就好了，也就是理解happens-before规则即可，其他复杂的内容有JMM规范结果操作系统叫我们搞定，我们只写好代码即可。

```java
	private int value=0;
	public void setValue(){
	    this.value=value;
	}
	public int getValue(){
	    return value;
	}
```

![](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328100616922.png)

- 把getter/setter方法都定义synchronized方法(某一时刻只能有一个线程进入,但是这样子也有问题，就是读取操作也只能一个线程进来)
- 把value定义为volatile变量,由于setter方法对value的修改不依赖value的原值,满足volatile关键字的使用
  (对一个volatile变量的写操作先行发生于后面对这个变量的读操作,前面的写对后面的读是可见的,这里的"后面"同样是指时间是的先后)

```java
private volatile int value = 0;

public int getValue(){
	return value // 利用volatile保证读取操作的可见性
}

public synchronized int setValue(){
	return ++value; // 利用synchronized保证符合操作的原子性
}
```

## 2.volatile

### 2.1 特点

- 可见性
- 有序性:有时候需要禁止重排

#### 2.1.1 内存语义

- 当写volatile变量时，JMM会把该线程对应的本地内存中的共享变量值**立即刷新回主内存**
- 当读取volatile变量时（当接收到写通知的时候），JMM会把该线程对应的本地内存设置为无效，重新回到主内存中读取最新的共享变量

### 2.2 内存屏障

使用内存屏障来保证可见性和有序性。

可见性：写完后立即刷新回主内村并且及时发出通知，大家都可以去主存拿到最新版本，前面的修改对后面的线程是可见的

重排序:是指编译器和处理器为了优化程序性能而对指令将那些重排序的一种手段，有时候回改变程序语句的先后顺序，不存在数据依赖关系，可以重排序。存在数据依赖关系，禁止重排序。但是重排序后的指令绝对不能改变原有的串行语义。这点在并发设计中必须要重点考虑。

#### 2.2.1 内存屏障是什么

内存屏障（也称内存栅栏,内存栅障,屏障指令等,是一类同步屏障指令,是CPU或编译器在对内存随机访问的操作中的一个同步点,使得此点之前的所有读写操作都执行后才可以开始执行此点之后的操作）,避免代码重排序。内存屏障其实就是一种JVM指令,Java内存模型的重排规则会要求Java编译器在生成JVM指令时插入特定的内存屏障指令,通过这些内存屏障指令,volatile实现了Java内存模型中的可见性和有序性,但volatile无法保证原子性

- 内存屏障之前的**所有写操作都要写回主内存**

- 内存屏障以后的**所有读操作都能获得内存屏障之前的所有写操作的最新结果（实现可见性）**
- 写屏障：告诉处理器在写屏障之前将所有存储在缓存中的数据同步到主内存中。也就是说当看到Store屏障指令，就必须把该指令之前所有的写入指令执行完毕后才能继续往下执行。
- 读屏障：处理器在读屏障以后的读操作，都在读屏障之后执行，也就是说在load屏障指令以后就能保证后面的读取数据指令一定能够读取到最新的数据。

因此重排序时，不允许把内存屏障以后的指令重排序到内存屏障之前。**一句话对一个volatile变量的写，先行发生于任何后续对于volatile变量的读，也叫写后读**



#### 2.2.2 内存屏障分类

- 粗分

  - 读屏障：在读指令之前插入读屏障，让工作内存或者CPU高速缓存当中的缓存数据失效，重新回到主内存中获取最新数据。
  - 写屏障：在写指令以后插入写屏障，强制把写缓冲区的数刷回主内存中去。

- 细分

  **落地是由volatile关键字,而volatile关键字靠的是StoreStore、StoreLoad 、LoadLoad、LoadStore四条指令**

  当我们的Java程序的变量被volatile修饰之后,会添加一个ACC_VOLATI LE,JVM会把字节码生成为机器码的时候,发现操作是volatile变量的话,就会根据JVM要求,在相应的位置去插入内存屏障指令
  

  ![image-20230328110143366](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328110143366.png)

  ​	![image-20230328110329081](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328110329081.png)

  - 读屏障

    - 在每个volatile读操作的后面插入一个LoadLoad屏障
    - 在每个volatile读操作的后面插入一个LoadStore屏障

    ![image-20230328112301407](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328112301407.png)

  - 写屏障

    - 在每个volatile写操作的前⾯插⼊⼀个StoreStore屏障，保证volatile写之前所有的，普通写操作都已经刷新到主内存中去
    - 在每个volatile写操作的后⾯插⼊⼀个StoreLoad屏障，避免了volatile写于后面有的volatile读/写操作进行重拍寻。

    ![image-20230328112451478](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328112451478.png)

#### 2.2.3 通过内存屏障保证有序性

- 重排序有可能影响程序的执行和实现的，因此我有时候希望告诉JVM不要进行重排序
- 对于编译器的重排序，JMM会根据重排序的规则，禁止特定类型的编译器重排序
- 对于处理器的重排序，java编译器在生成指令序列的适当位置，通过内存屏障指令，来禁止特定类型的处理器排序。

![image-20230328111151929](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328111151929.png)

| 当第一个操作为volatile读时,不论第二个操作是什么,都不能重排序。这个操作保证了volatile读之后的操作不会被重排到volatile读之前 |
| ------------------------------------------------------------ |
| 当第二个操作为volatile写时,不论第一个操作是什么,都不能重排序。这个操作保证了volatile写之前的操作不会被重排到volatile写之后 |
| 当第一个操作为volatile写时,第二个操作为volatile读时,不能重排 |

### 2.2. volatile特性

- 保证可见性

  - 说明：保证不同线程对某个变量完成操作后结果及时可见，即该共享内存一旦改变所有线程立即可见。

  - Code

    - 不加volatile，没有可见性，程序无法停止

      ```java
      public class VolatileSeeDemo {
          static boolean flag = true;
      
          public static void main(String[] args) {
              new Thread(()->{
                  System.out.println(Thread.currentThread().getName() + "\t ------come in");
                  while (flag){
      
                  }
                  System.out.println(Thread.currentThread().getName() + "\t ------come in");
              },"t1").start();
      
              flag = true;
      
              System.out.println(Thread.currentThread().getName() + "\t 修改完成 flag : " + flag);
          }
      }
      ```

      ![image-20230328145605065](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328145605065.png)

    - 加了volatile，保证可见性，程序时可以停止的

      ```java
      public class VolatileSeeDemo {
          static volatile boolean flag = true;
      
          public static void main(String[] args) {
              new Thread(()->{
                  System.out.println(Thread.currentThread().getName() + "\t ------come in");
                  while (flag){
      
                  }
                  System.out.println(Thread.currentThread().getName() + "\t ------come in");
              },"t1").start();
      
              flag = false;
      
              System.out.println(Thread.currentThread().getName() + "\t 修改完成 flag : " + flag);
          }
      }
      ```

      ![image-20230328150109974](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328150109974.png)

  - 原理解释：为什么线程t1为何看不到主线程main修改为false的flag的值呢

    - 1.主线程修改了flag以后没将其刷新到主内存，所以t1线程看不到
    - 2.主线程flag刷新到主内存，但是t1一直读取的时自己工作内存中的flag的值。没有去主内存中获取flag最新的值

  - **volatile读写过程**

    **read(读取)→load(加载)→use(使用)→assign(赋值)→store(存储)→write(写入)**→lock(锁定)→unlock(解锁)

    ![image-20230328151635149](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328151635149.png)

    read: 作用于主内存，将变量的值从主内存传输到工作内存，主内存到工作内存
    load: 作用于工作内存，将read从主内存传输的变量值放入工作内存变量副本中，即数据加载
    use: 作用于工作内存，将工作内存变量副本的值传递给执行引擎，每当JVM遇到需要该变量的字节码指令时会执行该操作
    assign: 作用于工作内存，将从执行引擎接收到的值赋值给工作内存变量，每当JVM遇到一个给变量赋值字节码指令时会执行该操作
    store: 作用于工作内存，将赋值完毕的工作变量的值写回给主内存
    write: 作用于主内存，将store传输过来的变量值赋值给主内存中的变量
    **由于上述6条只能保证单条指令的原子性，针对多条指令的组合性原子保证，没有大面积加锁，所以，JVM提供了另外两个原子指令**：
    lock: 作用于主内存，将一个变量标记为一个线程独占的状态，只是写时候加锁，就只是锁了写变量的过程。
    unlock: 作用于主内存，把一个处于锁定状态的变量释放，然后才能被其他线程占用

- 无原子性

  - volatile变量的符合操作不具有原子性，比如number++

  - Code

    ```java
    class MyNumber{
        volatile int number;
        public  void addPlusPlus(){
            number++;
        }
    }
    
    public class VolatileDemo2 {
        public static void main(String[] args) {
            MyNumber myNumber = new MyNumber();
    
            for (int i = 1; i <= 10 ;i++){
                new Thread(()->{
                    for (int j = 1; j <= 1000;j ++){
                        myNumber.addPlusPlus();
                    }
                },String.valueOf(i)).start();
            }
    
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
    
            System.out.println(myNumber.number);
        }
    }
    ```

    - #### 读取赋值一个普通变量的情况

      当线程1对主内存对象发起read操作到write操作第一套流程的时间里，线程2随时都有可能对这个主内存对象发起第二套操作。

      ![image-20230328152733218](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328152733218.png)

    - 不保证原子性

      从底层来说，i++或者number++（在执行引擎操作时）其实是分了三步的：*数据加载* 、*数据计算* 、*数据赋值* *。而这三步非原子操作

      ![image-20230328152908810](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328152908810.png)

      对于volatile变量具备可见性 ，JVM只是保证从主内存加载到线程工作内存的值是最新的，也仅是数据加载时是最新的。但是多线程环境下，“数据计算”和“数据赋值”操作可能多次出现，若数据在加载之后，若主内存volatile修饰变量发生修改之后，线程工作内存中的操作将会作废去读主内存最新值，操作出现写丢失问题。即各线程私有内存和主内存公共内存中变量不同步 ，进而导致数据不一致。由此可见volatile解决的是变量读取时的可见性问题，但无法保证原子性，对于多线程修改主内存共享变量的场景必须使用加锁同步。
      
    ```
      比如说你在计算的时候，别的线程已经提交了，所以你的计算直接失效了
    ```
    
      - **synchronized加了之后保证了串行执行，每次只有一个线程进来。**
    
      ![image-20230328154853830](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328154853830.png)
    
      - **但volatile不能保证原子性，大家一起读，一起加一，就看谁提交的快了。提交快的直接让另一个失效。**
      
        ![image-20230328155058520](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328155058520.png)
      
      - **从i++的字节码角度说明**
      
        ![image-20230328155148624](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328155148624.png)
      
      - **原子性指的是一个操作是不可中断的，即使是在多线程环境下，一个操作一旦开始就不会被其他线程影响。**
      
        ```java
        public void add()
        {
          i++; //不具备原子性，该操作是先读取值，然后写回一个新值，相当于原来的值加上1，分3步完成
        }
        ```
      
        如果第二个线程在第一个线程读取旧值和写回新值期间读取i的域值，那么第二个线程就会与第一个线程一起看到同一个值，
      
        并执行相同值的加1操作，这也就造成了线程安全失败，因此对于add方法必须使用synchronized修饰，以便保证线程安全.
  
  - 结论
  
    - volatile不适合参与到依赖当前值的运算，如i=i+1，i++之类的
  
    那么依靠可见性的特点volatile可以用在哪些地方呢？*通常volatile用作保存某个状态的boolean值或or int值。* **（一旦布尔值被改变迅速被看到，就可以做其他操作）**
  
    ![image-20230328160009691](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328160009691.png)
  
- 指令禁止重排

  重排序是指编译器和处理器为了优化程序性能而对指令序列进行重排序的一种手段，有时候会改变程序语句的先后顺序。

  ```
不存在数据依赖关系，可以重排序。
  
  ```

存量数据依赖关系，禁止重排序。
  ```

  数据依赖性 **：若两个操作访问同一变量，且这两个操作中有一个为写操作，此时两操作间就存在数据依赖性。**

  **但重排后的指令绝对不能改变原有的串行语义！这点在并发设计中必须要重点考虑!**

  - 重排序的分类和执行流程

  ![image-20230328163622877](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328163622877.png)

  
  
  1. 编译器优化的重排序:处理器不改变单线程串行语义的前提下，可以重新调整指令的执行顺序。
2. 指令级并行的重排序：处理器使用指令级并行技术来讲多条指令重叠执行，若不存在数据依赖性，处理器可以改变语句的对应的机器的执行顺序。
  3. 内存系统的重排序：由于处理器使用缓存和读/写缓冲区，这使得加载和存储操作上看上去可能是乱序执行。

  **存在数据依赖关系，禁止重排序===> 重排序发生，会导致程序运行结果不同。**
  
编译器和处理器在重排序时，会遵守数据依赖性，不会改变存在依赖关系的两个操作的执行,但不同处理器和不同线程之间的数据性不会被编译器和处理器考虑，其只会作用于单处理器和单线程环境，下面三种情况，**只要重排序两个操作的执行顺序，程序的执行结果就会被改变**。
  ![image-20230328164557228](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230328164557228.png)
  
  - Code
  
  ```java
  class VolatileTest{
      int i = 0;
      volatile boolean flag = false;
  
      public void  write(){
          i = 2;
          flag = true;
      }
  
      public void read(){
          if (flag){
              System.out.println("i = " + i);
          }
      }
  }
  ```

  - 写操作

  ![image-20230331145906198](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230331145906198.png)

  - 读操作
  
    ![image-20230331150210783](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230331150210783.png)

- 日常使用

  - 单一赋值可以，but含符合运算赋值不可以(i++之类)

    ```
    volatile int a = 10;
    volatile boolean flag = false;
    ```

  - 状态标志，判断也是是否结束

    ```java
    /*
    	使用:使用作为一个布尔值状态，用于知识发生了一个重要的一次性时间，例如完成初始化或者任务结束
    	理由:状态标志并不依赖于程序内任何其他状态，且通常只有一种状态
    	例子：任务是否结束
    */
    public class VolatileDemo3 {
        private volatile static boolean flag = true;
    
        public static void main(String[] args) throws InterruptedException {
            new Thread(()->{
                while (flag){
                    System.out.println("我的flag的值: " + flag);
                }
            },"t1").start();
    
            TimeUnit.SECONDS.sleep(2);
            new Thread(()->{
                flag = false;
            },"t2").start();
        }
    }
    ```

  - 开销较低的读，写锁策略

    ```
    public class Counter{
        private volatile int value;
    	
    	/*
    		使用：当读远多于xie，解释使用内部锁和volatile变量来减少同步的开销
    		理由：利用volatile保证读取操作的可见性，利用syncronized保证符合操作的原子性
    	*/
        public int getValue(){
            return value;
        }
    
        public synchronized int increment(){
            return value++;
        }
    }
    ```

  - DCL双端锁的发布

    - 问题实例

      ```java
      public class SafeDoubleCheckSingLeton {
          private static SafeDoubleCheckSingLeton singleton;
      
          // 私有化的构造方法
          private SafeDoubleCheckSingLeton(){
      
          }
      
          // 双重锁设计
          public static SafeDoubleCheckSingLeton getInstance(){
              if (singleton == null){
                  // 1.多线程并发创建对象时，会通过加锁来保证只有一个线程创建对象
                  synchronized (SafeDoubleCheckSingLeton.class){ // 1
                      if (singleton == null){ // 2
                          // 隐患：多线程环境下，由于重排序，该对象可能还未完成初始化就被其他线程读取
                          // 当然创建线程时候由于synchronization会被阻塞掉
                          // 但是仍然有可能由于指令重排，其他线程当singleton还没创建的时候就拿来使用。
                          singleton = new SafeDoubleCheckSingLeton(); // 3
                      }
                  }
              }
              return singleton;
          }
      }
      ```

    - 单线程条件

      单线程环境下(或者说正常情况下)，在“问题代码处”，会执行若下操作，保证能获取到自己初始化实例。

      ``` 
      memory = allocate() // 1.分类对象的内存空间
      crorInstance(memory) //2.初始化
      instance = memory() //3.设置instance指向分配的内存地址。
      ```

      

    - 多线程环境下，在“问题代码处”，会执行如下操作，由于重排序导致2，3乱序，后续就是其他线程得到的是null而不是完成初始化的对象

      - right

        ```
        memory = allocate() // 1.分类对象的内存空间
        crorInstance(memory) //2.初始化
        instance = memory() //3.设置instance指向分配的内存地址。
        ```

      - problem(重排序)

        ```=
        memory = allocate() // 1.分类对象的内存空间
        instance = memory() //3.设置instance只想刚分配的内存地址。
        crorInstance(memory) //2.初始化
        ```

      - 细节分析

        其中第三步实例化Singleton分多步（分配内存空间，初始化对象，将对象指向分配的内存空间）,某些百年一起为了性能原因，会将第二步和第三步进行重排序（分配内存空间，将对象指向分配的内存空间，初始化对象）。这样，某个线程可能会获得一个未完成的实例。

      - 解决方式

        ```java
        public class SafeDoubleCheckSingLeton {
        	// 重点 ！！！ 通过volatile 实现线程安全的线程初始化
            private volatile static SafeDoubleCheckSingLeton singleton;
        
            // 私有化的构造方法
            private SafeDoubleCheckSingLeton(){
        
            }
        
            // 双重锁设计
            public static SafeDoubleCheckSingLeton getInstance(){
                if (singleton == null){
                    // 1.多线程并发创建对象时，会通过加锁来保证只有一个线程创建对象
                    synchronized (SafeDoubleCheckSingLeton.class){ // 1
                        if (singleton == null){ // 2
                            // 隐患：多线程环境下，由于重排序，该对象可能还未完成初始化就被其他线程读取
                            // 当然创建线程时候由于synchronization会被阻塞掉
                            // 但是仍然有可能由于指令重排，其他线程当singleton还没创建的时候就拿来使用。
                            
                            // 解决隐藏原理：利用volatile，禁止“初始化对象“(2)和"设置singleton"指向内存空间(3)重排序
                            singleton = new SafeDoubleCheckSingLeton(); // 3
                        }
                    }
                }
                return singleton;
            }
        }
        ```

- 小总结

  - volatile可见性

  | volatile | 当对一个被volatile关键字修改的变量                           |
  | -------- | ------------------------------------------------------------ |
  | 1        | 写操作的话，这个变量的最新值会立即刷新回主内存               |
  | 2        | 读操作的话，总是能够读取这个变量的最新值，也就是这个变量最后被修改的值 |
  | 3        | 当某个线程收到通知，去读取volatile修饰的变量的值时候，线程私有工作内存数据失效，需要重新回到主内存去读取最新的数学。 |

  - volatile没有原子性

  - volatile禁止重排

    - 写指令

      ```java
      StoreStore屏障
      volatile
      StoreLoad屏障
      ```

      - StoreStore屏障:

        - 禁止上面的普通写和下面的volatile写操作重排序

        - 前面所有的普通写操作，数据都已经刷新到主内存。
        - 普通写和volatile写禁止重拍；volatile写和volatile写禁止重排

      - StoreLoad屏障:
        - 禁止上面的volatile写和下面的volatile读写或者普通写操作重排序
        - 前面volatile写的操作，数据都已经刷新到主内存
        - volatile写和普通写禁止重排序;volatile写和volatile读/写 禁止重排

    - 读指令

      ```
      volatile 读操作
      LoadLoad屏障
      LoadStore屏障
      ```

      - LoadLoad屏障：
        - 禁止下面的普通读，volatile读和上面的volatile读重排序
        - volatile读和普通读禁止重排
        - volatile读和volatile读禁止重排
      - LoadStore屏障
        - 禁止上面的volatile读和下面volatile写或者普通写重排序

  - 当编写volatile关键字;系统底层如何加入内存屏障?

    ![image-20230331233337520](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230331233337520.png)

  - 内存屏障是什么？

    是一种屏障指令，他使得CPU或者编译器对屏障指令前和后所发出的内存操作执行一个排序的约束，也叫内存栅栏或者栅栏指令。

  - 内存屏障能干嘛？
    - 阻止屏障两边的指令重排序
    - 写数据时加入屏障，强制将线程私有工作内存的数据刷回主物理内存
    - 写数据时加入屏障，线程私有工作内存的数据失效，重新到主内存内存中获取数据
  - 内存屏障四大指令
    - StoreStore
    - StoreLoad
    - LoadLoad
    - LoadStore
  - 一句话
    - volatile写之前的操作，都禁止重排序到volatile以后
    - volatile读以后的操作，都禁止重排序到volatile以前
    - volatile写以后的volatile读，禁止重排序
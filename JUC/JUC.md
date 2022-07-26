# JUC

## 1.JUC概述

### 1.1什么是JUC

在Java中，线程部分是一个重点，本片文章所说的JUC也是关于线程的。JUC就是java.util.concurrent工具包的简称。这是一个处理线程的工具包，JDK1.5开始出现的。

## 1.2线程和进程概念

- 进程

  进程是计算机中的程序关于某数据集合上的一次运行活动，是系统进项资源分配和调度的基本单位，是操作系统结构的基础。在当面向线程设计的计算机结构中，进程是线程的容器。程序是指令、数据以及其组指形式的描述，进程是程序的实体。

- 线程

  线程是操作系统能进项运算调度的最小单位。他被包含在进程之中，是进程中的实际运作单位。一条线程是指进程中一个单一顺序的控制流，一个进程中可以并发多个线程，每条线程可以执行不同的任务。

- 管程

  管程在功能上和信号量及PV操作类似，属于一种进程同步互斥工具，但是具有与信号量及PV操作不同的属性。

  - 管程被引入的原因

    信号量机制的缺点：进程自备同步操作，P(S)和V(S)操作大量分散在各个进程中，不易管理，易发生死锁。1974年和1977年，Hore和Hansen提出了管程。

    管程特点：管程封装了同步操作，对进程隐蔽了同步细节，简化了同步功能的调用界面。用户编写并发程序如同编写顺序(串行)程序。

    引入管程机制的目的：

    - 把分散在各进程中的临界区集中起来进行管理
    - 防止进程有意或无意的违法同步操作
    - 便于用高级语言来书写程序，也便于程序正确性验证。

- 线程状态

  新建、运行、阻塞、等待、超时等待、结束

- wait和sleep方法

  - sleep是Thread的静态方法，wait是Object的方法，任何对象实例都能调用。
  - sleep不会释放锁，他也不需要占用锁。wait会始放锁，但是调用它的前提是当前线程站有锁(代码要在synchronized中)
  - 他们都可以被interrupted方法打断

- 用户线程和守护线程

  - 用户线程:自定义线程， **如果主线程死去，用户线程还存活，jvm不会死去**
  - 守护线程：运行在后台的线程，比如说垃圾回收，**如果主线程死去，没有用户线程了，都是守护线程，jvm就会结束了**


## 2.Lock接口

### 2.1 Synchronized关键字回顾

**Synchronized是Java中的关键字，是一种同步锁机制。它修饰的对象有以下几种:**

- 修饰一个代码块，被修饰的代码块成为同步语句块，起作用的范围是大括号{}括起来的代码，作用的对象是调用这个代码块的对象。

- 修饰一个方法，被修饰的方法成为同步方法，其作用的范围是整个方法，作用的对象是调用这个方法的对象。

  **虽然可以使用synchronized来定义方法，但是synchronized并不属于方法定义的一部分，因此synchronized关键字不能被继承。如果在父类中的某个方法使用了synchronized关键字，而在子类中覆盖了这个方法，在子类中的这个方法默认情况并不是同步的，而必须显性的在子类的方法中也加入synchronized关键字才可以。但是可以调用父类的方法，那么必然是同步的。**

- 实操

  ```java
  /**
   * 第一步创建资源类,定义属性和操作方法
   */
  class Ticket{
      // 票数
      private int num = 30;
      //操作方法:卖票
      public synchronized void sale(){
          if(num > 0){
              System.out.println(Thread.currentThread().getName()+"----剩下"+--num);
          }
      }
  }
  
  public class SellTicket {
      /**
       * //第二部 创建多个线程 调用资源类的方法
       */
      public static void main(String[] args) {
          //创建资源对象
          Ticket ticket = new Ticket();
          //创建三个线程
          new Thread(new Runnable() {
              @Override
              public void run() {
                 for (int i = 0; i < 40;i++){
                     ticket.sale();
                 }
              }
          },"AA").start();
  
          new Thread(new Runnable() {
              @Override
              public void run() {
                  for (int i = 0; i < 40;i++){
                      ticket.sale();
                  }
              }
          },"BB").start();
  
  
          new Thread(new Runnable() {
              @Override
              public void run() {
                  for (int i = 0; i < 40;i++){
                      ticket.sale();
                  }
              }
          },"CC").start();
      }
  }
  ```
  

### 2.2 Lock初认识

Lock锁实现提供了比使用同步方法和语句可以获得更为广泛的锁操作。它们允许更灵活的结构，可能具有非常不同的属性，并且可能支持多个关联条件对想。Lock'提供了比synchronized更多的功能。

- Lock不是java语言内置的，synchronized是java语言的关键字，因此是内置特性。Lock是一个类，通过这个类可以实现同步访问。
- Lock和synchronized有一点非常大的不同，采用synchronized不需要用户去手动释放锁，当synchronized方法或者synchronized代码块执行完了以后，系统会自动让线程始放对锁的占用；而Lock必须用户去手动释放锁，如果没有主动释放锁，就有可能导致死锁的现象。**于是，可以知道synchronized在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象的发生；而Lock在发生异常时，如果没有主动通过unLock()去释放锁，则很可能导致死锁，则需要在finally代码块中释放锁**
- Lock可以让等待锁的线程中断，而synchronized却不行，使用synchronized时，等待的线程会一直等待下去，不能够响应中断。
- 通过Lock可以知道有没有成功获取锁，而synchronize却无法知道。
- Lock可以提高多个线程进行读的操作的效率。**如果竞争的不激烈，两者的性能差不多的，而当竞争资源非常激烈时，Lock的性能要远远优于synchronized**

- 实操

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LTicket{
    //票的数量
    int number = 30;
    //可重用锁
    private final Lock lock = new ReentrantLock();
    //卖票的方法
    public void sale(){
        //上锁
        lock.lock();
        try {
            if (number > 0){
                System.out.println(Thread.currentThread().getName()+"：剩余"+--number);
            }
        }catch (Exception e){
            System.out.println("我错了");
        }finally {
            //解锁
            lock.unlock();
        }

    }
}

public class LSaleTicket {
    //第二步 创建多个线程 调用资源类的操作方法
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println(start);
        LTicket ticket = new LTicket();
        new Thread(()->{
            for (int i = 0; i < 40 ;i++){
                ticket.sale();
            }
        },"AA").start(); //再调用start之前，线程不一定马上创建。

        new Thread(()->{
            for (int i = 0; i < 40 ;i++){
                ticket.sale();
            }
        },"BB").start();

        new Thread(()->{
            for (int i = 0; i < 40 ;i++){
                ticket.sale();
            }
        },"CC").start();
    }
}
```

### 2.3 线程间的通信

#### 2.3.1wait方法和notifyAll简单实现

通过object类的wait方法和notifyAll完成线程的相互通知，按照以下思路。

1. 编写资源类定义、资源方法
2. 在资源方法中完成 判断，干活，通知
3. 创建线程调用资源类的方法。

```java
//第一步 创建资源类 定义属性和方法
class Share{
    //初始值
    private int number = 0;
    //+1方法
    public synchronized void incr() throws InterruptedException {
        // 第二步 判断 干活 通知
        if(number != 0){ //判断number值是否为0，如果不是0，则等待
            this.wait();
        }
        // 如果number是0，就+1操作
        number++;
        System.out.println(Thread.currentThread().getName()+"::"+number);
        //通知其他线程
        this.notifyAll();
    }
    //-1方法
    public synchronized void decr() throws InterruptedException {
        if(number != 1){
            this.wait();
        }
        //干活
        number--;
        System.out.println(Thread.currentThread().getName()+"::"+number);
        //通知其他线程
        this.notifyAll();
    }
}

public class ThreadDemo1 {
    //创建多个线程,调用资源类的操作方法
    public static void main(String[] args) {
        Share share = new Share();
        //创建线程
        new Thread(()->{
            for (int i = 1;i <= 10;i++){
                try {
                    share.incr();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        },"AAA").start();
        //创建线程
        new Thread(()->{
            for (int i = 1;i <= 10;i++){
                try {
                    share.decr();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        },"BBB").start();
    }
}
```

#### 2.3.2 虚假唤醒问题

**如果像上述代码一个生产者 一个消费者进行线程切换，是会输出1，0，1，0的，但是如果有，为了避免虚假唤醒应该将资源方法的if改写成while，虚假唤醒的原因是wait会哪里等待，哪里被唤醒**

```java
package sellTicket.lock;

//第一步 创建资源类 定义属性和方法
class Share{
    //初始值
    private int number = 0;
    //+1方法
    public synchronized void incr() throws InterruptedException {
        // 第二步 判断 干活 通知
        while(number != 0){ //判断number值是否为0，如果不是0，则等待
            this.wait();
        }
        // 如果number是0，就+1操作
        number++;
        System.out.println(Thread.currentThread().getName()+"::"+number);
        //通知其他线程
        this.notifyAll();
    }
    //-1方法
    public synchronized void decr() throws InterruptedException {
        while (number != 1){
            this.wait();
        }
        //干活
        number--;
        System.out.println(Thread.currentThread().getName()+"::"+number);
        //通知其他线程
        this.notifyAll();
    }
}

public class ThreadDemo1 {
    //创建多个线程,调用资源类的操作方法
    public static void main(String[] args) {
        Share share = new Share();
        //创建线程
        new Thread(()->{
            for (int i = 1;i <= 10;i++){
                try {
                    share.incr();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        },"AAA").start();

        //创建线程
        new Thread(()->{
            for (int i = 1;i <= 10;i++){
                try {
                    share.decr();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        },"BBB").start();

        //创建线程
        new Thread(()->{
            for (int i = 1;i <= 10;i++){
                try {
                    share.decr();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        },"CCC").start();

        //创建线程
        new Thread(()->{
            for (int i = 1;i <= 10;i++){
                try {
                    share.decr();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        },"DDD").start();
    }
}
```

#### 2.3.3 Lock实现进程间通信

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Share{
    private int number =0;

    //创建Lock
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    //+1
    public void incr() throws InterruptedException{
        //上锁
        lock.lock();
        try {
            //判断
            while (number != 0){
                condition.await();
            }
            //干活
            number++;
            System.out.println(Thread.currentThread().getName()+"::"+number);
            //通知
            condition.signalAll();
        }finally {
            //解锁
            lock.unlock();
        }
    }

    public void decr() throws InterruptedException{
        lock.lock();
        try {
            //判断
            while (number != 1){
                condition.await();
            }
            //操作
            number--;
            System.out.println(Thread.currentThread().getName()+"::"+number);
            //通知
            condition.signalAll();
        }finally {
            //解锁
            lock.unlock();
        }
    }
}

public class ThreadDemo2 {
    public static void main(String[] args) {
        Share share = new Share();
        new Thread(()->{
            for (int i = 0;i <= 10 ;i++){
                try {
                    share.incr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"AAA").start();

        new Thread(()->{
            for (int i = 0;i <= 10 ;i++){
                try {
                    share.decr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"BBB").start();

        new Thread(()->{
            for (int i = 0;i <= 10 ;i++){
                try {
                    share.decr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"CCC").start();

        new Thread(()->{
            for (int i = 0;i <= 10 ;i++){
                try {
                    share.decr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"DDD").start();
    }
}
```

#### 2.3.4 线程间定制化启动

- 启动要求

  AA打印5次，BB打印10次，CC打印15次

- 要求分析

  **需要按照顺序**

- 代码实践

```java
//第一步 创建资源类
class ShareResource {
    //定义标志位
    private int flag = 1; //1 AA 2 BB 3 CC
    // 创建Lock锁
    private Lock lock = new ReentrantLock();
    //  创建三个condition
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();
    // 打印5次。参数第几轮
    public void print5(int loop) throws InterruptedException{
        //上锁
        lock.lock();
        try {
            // 判断
            while (flag != 1){
                c1.await();
            }
            // 干活
            for (int i = 0;i < 5 ;i++){
                System.out.println(Thread.currentThread().getName()+"::"+loop);
            }
            // 通知
            flag = 2 ;
            c2.signal();
        }finally {
            //解锁操作 不然可能造成死锁
            lock.unlock();
        }
    }

    //打印10次,参数第几轮
    public void print10(int loop) throws InterruptedException{
        // 上锁
        lock.lock();
        try {
            //判断
            while (flag != 2){
                c2.await();
            }
            // 干活
            for (int i = 0;i < 10 ;i++){
                System.out.println(Thread.currentThread().getName()+"::"+loop);
            }
            // 通知
            flag = 3;
            c3.signal();
        }finally {
            lock.unlock();
        }
    }

    //打印10次,参数第几轮
    public void print15(int loop) throws InterruptedException{
        // 上锁
        lock.lock();
        try {
            //判断
            while (flag != 3){
                c3.await();
            }
            // 干活
            for (int i = 0;i < 15 ;i++){
                System.out.println(Thread.currentThread().getName()+"::"+loop);
            }
            // 通知
            flag = 1;
            c1.signal();
        }finally {
            lock.unlock();
        }
    }
}
public class ThreadDemo3 {
    public static void main(String[] args) {
        ShareResource shareResource = new ShareResource();
        new Thread(()->{
            for (int i = 1;i <= 10 ;i++){
                try {
                    shareResource.print5(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"AA").start();

        new Thread(()->{
            for (int i = 1;i <= 10 ;i++){
                try {
                    shareResource.print10(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"BB").start();


        new Thread(()->{
            for (int i = 1;i <= 10 ;i++){
                try {
                    shareResource.print15(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"CC").start();
    }
}

```

**当然按照一个condition也能够满足输出条件，但是通知的过程中可能出现多个进程争抢资源，而某个应得到资源的线程无法被唤醒的问题**

#### 2.3.5 集合线程不安全问题的总结(以ArrayList作为参考)

```java
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ThreadDemo4 {
    public static void main(String[] args) {
        //创建ArrayList集合
        List<String> list = new ArrayList<>();

        for (int i = 0;i < 10;i++){
            new Thread(()->{
                //向集合中添加内容
                list.add(UUID.randomUUID().toString().substring(0,8));
                //从集合中获取内容
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}
```

当你执行代码时，会报错**java.util.ConcurrentModificationException**,原因是你一边读取，一边向ArrayList对象中写入元素

- 解决方法1：将ArrayList改成Vector对象

**Vector对象能够是线程安全的原因是，Vector的方法是有synchronized关键字修饰，但是目前来说不建议这种方法，synchronized是重量级的效率较低**

```java
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class ThreadDemo4 {
    public static void main(String[] args) {
        //创建Vector集合
        List<String> list = new Vector<>();
        for (int i = 0;i < 10;i++){
            new Thread(()->{
                //向集合中添加内容
                list.add(UUID.randomUUID().toString().substring(0,8));
                //从集合中获取内容
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}
```

- 解决方法2:使用Collection.synchronized方法（也不常用）

```java
import java.util.*;

public class ThreadDemo4 {
    public static void main(String[] args) {
        //Collections.synchronizedList的使用
        List<String> list = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0;i < 10;i++){
            new Thread(()->{
                //向集合中添加内容
                list.add(UUID.randomUUID().toString().substring(0,8));
                //从集合中获取内容
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}
```

- 解决方案3：CopyOnWriteArrayList

```java
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThreadDemo4 {
    public static void main(String[] args) {
        // CopyOnWriteArrayList的使用
        List<String> list = new CopyOnWriteArrayList<>();
        for (int i = 0;i < 10;i++){
            new Thread(()->{
                //向集合中添加内容
                list.add(UUID.randomUUID().toString().substring(0,8));
                //从集合中获取内容
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}
```

**CopyOnWriteArrayList读的时候支持并发读，当读并发的时候，当需要写的时候，需要将数据进行复制一遍，然后对这个副本独立写，写完了以后进行合并。简称读时共享，写时复制**

#### 2.3.6 HashSet线程不安全的问题总结

- 解决方法1:CopyOnWriteArraySet

```java
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ThreadDemo4 {
    public static void main(String[] args) {
        // CopyOnWriteArrayList的使用
        Set<String> set = new CopyOnWriteArraySet<>();
        for (int i = 0;i < 10;i++){
            new Thread(()->{
                //向集合中添加内容
                set.add(UUID.randomUUID().toString().substring(0,8));
                //从集合中获取内容
                System.out.println(set);
            },String.valueOf(i)).start();
        }
    }
}
```

#### 2.3.7 HashMap线程不安全的问题总结

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadDemo4 {
    public static void main(String[] args) {
        // CopyOnWriteArrayList的使用
        Map<String,String> map = new ConcurrentHashMap<>();
        for (int i = 0;i < 10;i++){
            String key = String.valueOf(i);
            new Thread(()->{
                //向集合中添加内容
                map.put(key,UUID.randomUUID().toString().substring(0,8));
                //从集合中获取内容
                System.out.println(map);
            },String.valueOf(i)).start();
        }
    }
}
```

#### 2.3.8 可重入锁

**synchronized(隐式)和Lock(显式)都是可重入锁**

```
public class ThreadDemo4 {
    public static void main(String[] args) {
        Object object = new Object();
        new Thread(()->{
            synchronized (object) {
                System.out.println(Thread.currentThread().getName()+"外层");
                synchronized (object){
                    System.out.println(Thread.currentThread().getName()+"中层");
                    synchronized (object){
                        System.out.println(Thread.currentThread().getName()+"内层");
                    }
                }
            }

        },"t1").start();
    }
}
```

打印结果：

外层

中层

内层

**值得注意的是可重用锁指的是一个线程可以重复获取锁，但是如果两个线程，一个线程在没有释放锁的情况下，另外的一个线程则无法获取锁，将会卡住。**

## 3.Callable

创建线程多种方式

第一种：继承Thread类

第二种：实现Runnable

第三种：Callable接口

第四种：线程池方式

- Runnable接口和Callable接口
  - Callable有返回值，Ruunable无返回值
  - Callable无法计算结果会抛出异常，Runnable不会抛出异常
  - 实现方法名称不同，一个是run方法，一个是call方法

```java
class MyThread1 implements Runnable{
    @Override
    public void run(){

    }
}

class MyThread2 implements Callable{
    @Override
    public Integer call() throws Exception{
        return 200;
    }
}

public class Demo1 {
    public static void main(String[] args) {
        new Thread(new MyThread1(),"AA").start();
        // 报错
        new Thread(new MyThread2(),"BB").start();
    }
}
```

**发现MyThread2（）的使用传参到Thread中是无法创建线程的，所以失败了，需要使用用FutureTask类**

```java
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class MyThread1 implements Runnable{
    @Override
    public void run(){

    }
}

class MyThread2 implements Callable{
    @Override
    public Integer call() throws Exception{
        return 200;
    }
}


public class Demo1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new Thread(new MyThread1(),"AA").start();

//        new Thread(new MyThread2(),"BB").start();
        FutureTask<Integer> futureTask1 = new FutureTask<>(new MyThread2());
        // Callable默认是函数式接口 可以使用lam表达式完成
        FutureTask<Integer> futureTask2 = new FutureTask<>(()->{
            System.out.println(Thread.currentThread().getName()+"COME IN CALLABLE");
            return 1024;
        });

        // 创建一个线程
        new Thread(futureTask2,"Luck").start();

        while (!futureTask2.isDone()){
            System.out.println("wait...");
        }

        // 获得返回call函数里面的值
        System.out.println(futureTask2.get());

        // 获得返回call函数里面的值,第二次不需要计算直接返回
        System.out.println(futureTask2.get());

        // 调用FutureTask的get方法
        System.out.println(Thread.currentThread().getName()+ "come over");
    }
}

```

## 4.辅助类

### 4.1 减少计数CountDownLatch

CountDownLatch可以设置一个计数器，然后通过countDown方法来进行减1的操作，使用await方法等待计数器不大于0，然后继续执行await方法以后的语句。

- CountDownLatch主要有两个方法，当一个或者多个线程调用await方法时，这些线程会阻塞。
- 其他线程调用countDown方法会将计数器减1(调用countDown方法的线程不会阻塞)
- 当计数器的值变为0的时候，因await方法阻塞的线程会被唤醒，继续执行。

```java
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        //传入计数器的值
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 1; i <= 6;i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"号同学离开了");
                //每次让计数器的值减一
                countDownLatch.countDown();
            },String.valueOf(i)).start();
        }
        //等待
        countDownLatch.await();
        System.out.println("班长关门走了");
    }
}
```

**采用CountDownLatch能有效的帮助主线程在子线程的运行以后开始运行。**

### 4.2 循环栏删CyclicBarrier

在使用中CyclicBarrier的构造方法第一个目标参数时目标障碍数，每次执行cyclicBarrier.await()以后，则相当于障碍数加一，当障碍数等于目标障碍数的时候，就会调用创建CyclicBarrier的参数Runable接口。

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    private static final int NUMBER = 7;
    public static void main(String[] args) {
        // 创建CyclicBarrier
        CyclicBarrier cyclicBarrier = new CyclicBarrier(NUMBER,()->{
            System.out.println("集齐了七颗龙珠");
        });
        // 创建七颗龙珠的过程
        for (int i = 0;i <= 7;i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+" 星龙珠被收集到了");
                //等待方法
                try {
                    // 此线程进入等待队列
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
        }
    }
}
```

### 4.3 信号灯Semaphone

```java
// 六个汽车 三个车位
public class SemaphoreDemo {
    public static void main(String[] args) {
        // 创建Semaphore，设置条件许可
        Semaphore semaphore = new Semaphore(3);
        // 模拟六辆车
        for (int i = 1 ; i <= 6;i++){
            new Thread(()->{
                try{
                    // 抢占
                    semaphore.acquire();

                    System.out.println(Thread.currentThread().getName()+" 抢到车位");

                    //设置停车时间
                    TimeUnit.SECONDS.sleep(new Random().nextInt(5));

                    System.out.println(Thread.currentThread().getName()+" 离开车位");
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    // 释放
                    semaphore.release();
                }
            },String.valueOf(i)).start();
        }
    }
}
```


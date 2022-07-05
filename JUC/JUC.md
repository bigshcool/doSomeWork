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

```
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

```
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

```
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

```
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








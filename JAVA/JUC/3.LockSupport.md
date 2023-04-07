# LockSupport

## 1.什么是中断机制？

首先

一个线程不应该由其他线程来强制中断或者停止，而是应该**由线程自己自行停止**，自己来决定自己的命运。

所以，Thread.stop,Thread.suspend,Thead.resume都已经被废弃了。



其次

在java中没有办法立即停止一条线程，然而停止线程却显得尤为重要，如取消一个耗时操作。

因此，Java提供了一种用于停止线程的协商机制——中断，也即中断标识协商机制。



**中断只是一条协商协商机制，Java没有给中断增加任何语法，中断的过程需要程序员完全自己实现**

若要中断一个线程，你需要手动调用该线程的interrupt方法，该方法也仅仅是将线程对象的中断标识位设置为true；

接着你需要自己写代码不断检测当前线程的标识位，如果为true，表示别的线程请求这条线程中断。

此时究竟需要做什么需要你自己写代码完成。



每个线程对象中都有一个中断标识位，表示该线程是否中断，该标识位为true表示中断，为false表示未中断。

通过调用线程对象的interrupt方法将该线程的标为设为true，可以在别的线程中调用，也可以在自己的线程中调用。



## 2.中断方法说明

| 返回值         | 函数            | 说明                                                         |
| -------------- | --------------- | ------------------------------------------------------------ |
| void           | interrupt()     | 中断此线程，实例方法interrupt()仅仅是设置线程中的中断状态未true，发起协商而不会立刻停止线程 |
| static boolean | interrupted     | 测试当前线程是否已被中断，这个方法做了两件事：1.返回当前线程的中断状态，测试当前线程线程是否被中断。2.将当前线程的中断状态清零并且设置未false，清楚线程的中断状态。如果连续两次调用这个方法，第二次将会返回false，因为连续调用两次的结果可能不一样。 |
| boolean        | isInterrupted() | 测试此线程是否已被中断                                       |

### 2.1 如何停止中断运行的线程

#### 2.1.1 通过一个volatile变量实现

```java
import java.util.concurrent.TimeUnit;

public class InterruptDemo {
    static volatile boolean isStop = false;
    public static void main(String[] args) throws InterruptedException {
        new Thread( ()->{
            while (true){
                if (isStop){
                    System.out.println(Thread.currentThread().getName() + "\t isStop被修改为True，程序停止" );
                    break;
                }
                System.out.println("---hello volatile");
            }
        },"t1").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread( ()->{
            isStop = true;
        },"t2").start();
    }
}
```

#### 2.1.2 通过AtomicBoolean

```java
public class InterruptDemo {
    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    public static void main(String[] args) throws InterruptedException {
        new Thread( ()->{
            while (true){
                if (atomicBoolean.get()){
                    System.out.println(Thread.currentThread().getName() + "\t isStop被修改为True，程序停止" );
                    break;
                }
                System.out.println("---hello volatile");
            }
        },"t1").start();
        TimeUnit.SECONDS.sleep(1);
        new Thread( ()->{
            atomicBoolean.set(true);
        },"t2").start();
    }
}
```

#### 2.1.3 通过Thread类自带的中断Api实例方法实现

在需要中断的线程这种不断监听中断状态，一旦发生中断，就执行响应的中断处理业务逻辑stop线程。

```java
import java.util.concurrent.TimeUnit;

public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + "\t isInterrupted被修改为True，程序停止");
                    break;
                }
                System.out.println("---hello interrupt");
            }
        }, "t1");

        t1.start();

        TimeUnit.SECONDS.sleep(1);
        new Thread( ()->{
            t1.interrupt();
        },"t2").start();
    }
}
```

### 2.2 interrupt和isInterrupted源码分析

#### 2.2.1 interrupt

```
public void interrupt()
```

中断此线程。

除非当前线程正在中断（始终允许）， [否则](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Thread.html#checkAccess())将调用此线程的[`checkAccess`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Thread.html#checkAccess())方法，这可能会导致抛出[`SecurityException`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/SecurityException.html) 。

如果该线程阻塞的调用[`wait()`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Object.html#wait()) ， [`wait(long)`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Object.html#wait(long)) ，或[`wait(long, int)`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Object.html#wait(long,int))的方法[`Object`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Object.html)类，或的[`join()`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Thread.html#join()) ， [`join(long)`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Thread.html#join(long)) ， [`join(long, int)`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Thread.html#join(long,int)) ， [`sleep(long)`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Thread.html#sleep(long)) ，或[`sleep(long, int)`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/Thread.html#sleep(long,int)) ，这个类的方法，那么它的中断状态将被清除，并且将收到[`InterruptedException`](https://www.runoob.com/manual/jdk11api/java.base/java/lang/InterruptedException.html) 。

如果在[`InterruptibleChannel`上](https://www.runoob.com/manual/jdk11api/java.base/java/nio/channels/InterruptibleChannel.html)的I / O操作中阻止该线程，则通道将关闭，线程的中断状态将被设置，线程将收到[`ClosedByInterruptException`](https://www.runoob.com/manual/jdk11api/java.base/java/nio/channels/ClosedByInterruptException.html) 。

如果该线程在[`Selector`中](https://www.runoob.com/manual/jdk11api/java.base/java/nio/channels/Selector.html)被阻塞，则线程的中断状态将被设置，它将立即从选择操作返回，可能具有非零值，就像调用选择器的[`wakeup`](https://www.runoob.com/manual/jdk11api/java.base/java/nio/channels/Selector.html#wakeup())方法一样。

如果以前的条件都不成立，则将设置该线程的中断状态。

中断不活动的线程不会产生任何影响。

- **异常**

  `SecurityException` - 如果当前线程无法修改此线程

```java
public void interrupt() {
       if (this != Thread.currentThread())
        checkAccess();

       synchronized (blockerLock) {
           Interruptible b = blocker;
           if (b != null) {
               interrupt0();           // Just to set the interrupt flag
               b.interrupt(this);
               return;
           }
        }
       interrupt0();
    }
```

1.总体而言:如果线程处于**正常活动状态**，那么会将该线程的中断标志设置为true，仅此而已。**如果设置中断标志的线程将继续持续正常运行，不受影响，所以Interrupt并不能真正的中断线程，需要被调用的线程自己进行配合才行，在题目中看其实就是break操作。**

2.如果线程处于被阻塞状态(例如处于sleep,wait,join等状态)，在别的线程中调用当前线程对象的interrupt方法，那么线程立即退出被阻塞状态，并且抛出一个InterruptedException异常。

#### 2.2.2 isInterrrupted

public boolean isInterrupted()

测试此线程是否已被中断。 线程的*中断状态*不受此方法的影响。

线程中断被忽略，因为在中断时线程不活动将被此方法反映返回false。

- **结果**

  `true`如果此线程被中断; 否则为`false` 。

#### 2.2.3 验证调用interrupt后，线程不会立刻结束

```java
import java.util.concurrent.TimeUnit;

public class InterruptDemo2 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i <= 300; i++){
                System.out.println("-----------" + i);
            }
        }, "t1");

        t1.start();

        TimeUnit.MICROSECONDS.sleep(1);
        new Thread( ()->{
            t1.interrupt();
        },"t2").start();
        TimeUnit.MICROSECONDS.sleep(2);

        System.out.println("t1线程调用interrupt()后的中断标志01" + t1.isInterrupted()); //true

        TimeUnit.MICROSECONDS.sleep(2000);

        System.out.println("t1线程不活动的时候" + t1.isInterrupted()); //false
    }
}
```

#### 2.2.4 深入了解interrupt打断阻塞线程时处理

```java
public class InterruptDemo3 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + "被打断");
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();//由于在阻塞的时候调用了打断方法，那么程序会把中断标志位进行清空，所以不设置interrupt一次会无线循环
                    e.printStackTrace();
                }
                System.out.println("lll");
            }
        }, "a");
        t1.start();
        TimeUnit.SECONDS.sleep(2);
        new Thread(()->{
            t1.interrupt();
        },"b").start();
    }
}
```

## 3.LockSupport详解

LockSupport是用来创建锁和其他同步类的基本线程阻塞原语。

### 3.1 线程等待唤醒机制

###  3.1.1 使用Object中的wait()方法让线程等待，notify唤醒线程

```java
import java.util.concurrent.TimeUnit;

public class Demo1 {
    public static void main(String[] args) {
        Object o = new Object();

        new Thread(()->{
            synchronized (o){
                System.out.println(Thread.currentThread().getName() + "\t ------ come in ----");
                try {
                    // 会释放锁
                    o.wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t ------ 被唤醒");
            }
        },"a").start();

        // 暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        new Thread(()->{
            synchronized (o){
                o.notify();
                System.out.println(Thread.currentThread().getName() +"\t ---- 发出通知");
            }
        },"b").start();

    }
}
```

**wait和notify必须要写在同步代码块中，并且wait要在notify之前执行**

### 3.1.2 await和signal实现等待和唤醒

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 出现的问题和wait方法一样
public class Demo1 {
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();

        Condition condition = lock.newCondition();

        new Thread( () -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t ---- come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t ---- come in");
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        },"a").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread( () -> {
            lock.lock();
            try {
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "\t ---- 发生消息");
            }finally {
                lock.unlock();
            }
        },"b").start();
    }
}
```

### 3.1.3 LockSupport之park和unpark编码实战

```java
// 无需在同步代码块中
// 无需讲究顺序
public class Demo1 {
    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t" + System.currentTimeMillis());
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + "\t" + System.currentTimeMillis() + "----被叫醒");
        }, "a");
        a.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        LockSupport.unpark(a);
        System.out.println(Thread.currentThread().getName() + "\t" + System.currentTimeMillis() + "----unpark over");
    }
}
```

但是park和unpark需要成对出现呢，同时你知道许可证不会累计，只需要一对就ok了。

- 为什么可以突破wait/notify的原有调用顺序？

  因为unpark获得了一个凭证，以后再调用park方法，就可以名正言顺的凭证消费，故不会阻塞。先发放了凭证后续可以畅通无阻。

- 为什么唤醒两次后阻塞两次，但最终结果还会阻塞线程？

  因为凭证的数据最多为1，连续调用两次unpark和调用一次unpark效果一样，指挥增加一个凭证；而调用两次park却需要消费两个凭证，凭证不够，不能放行。


# CAS

## 1,原子类

java.util.concurrent.atomic

### 1.1 没有CAS之前

多线程环境不使用原子类保证线程安全i++(基本数据类型)

```java
public class AtomicTest {
    volatile int number = 0;
    
    public int getNumber(){
        return number;
    }
    // 为了保证数据的一致性和原子性
    public synchronized void setNumber(){
        number++;
    }
}
```

问题：synchronized的重量级可能太大了

### 1.2 使用CAS

```java
public class AtomicTest {
    AtomicInteger atomicInteger = new AtomicInteger();

    public int getAtomicInteget(){
        return atomicInteger.get();
    }

    public void setAtomicInteger(){
        atomicInteger.getAndIncrement();
    }
}
```

没有加synchronized重量级锁，所以他的性能更好，类似于我们的乐观锁。



### 1.3 CAS是什么？

compare and swap的缩写，中文翻译时比较并交换，实现并发算法时常用到的一种技术。

它包含三个操作数：**内存位置，预期原值以及更新值**

执行CAS操作的时候，将内存位置的值与预期原值相比较：

- 如果匹配，那么处理器将会自动将该位置的值跟新为新值
- 如果不匹配，处理器不做任何操作，多个线程同时执行CAS操作只会有一个成功。

![image-20230401093851158](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401093851158.png)



### 1.4 CASdemo

```java
public class AtomicTest {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        // 原值是5，期望值是5，更新值是200
        // 所以满足条件，直接更新
        System.out.println(atomicInteger.compareAndSet(5,200) + "\t" +atomicInteger.get());
        // 原值是200，期望值是5，更新值是300
        // 所以不满足条件更新失败
        System.out.println(atomicInteger.compareAndSet(5, 300) + "\t" + atomicInteger.get());
    }
}
```

![image-20230401095211708](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401095211708.png)

CAS是JDK提供的非阻塞原子性操作，它通过硬件保证了比较-跟新的原子性。

它是非阻塞的且自身具有原子性，也就是说这玩意效率更高且通过硬件保证，说明就这玩意更为可靠。



CAS是一条CPU的原子指令**(cmpxchg指令)**,不会造成所谓的数据不一致问题，Unsafe提供CAS方法(如compareAndSwapXXX)底层实现为CPU指令cmpxchg。

执行指令cmpxchg指令的时候，回判断当前系统是否为多核系统，如果是就给总线加锁，只有一个线程会对总线枷锁成功，加锁成功以后会执行CAS操作，也就是CAS的原子性实际上CPU实现独占的，比起用snchronized重量级锁，这里的排他时间将会短很多，所以在多线程情况下的性能会更好。



## 2.CAS源码分析

### 2.1 unsafe简要方法

compareAndSet()方法的源码

```java
/**
var1：表示要操作的对象
var2: 表示要操作对象中属性地址的偏移量
var4: 表示需要修改数据的期望的值。
var5/var6: 表示需要修改的新值。
*/

public final native boolean compareAndSwapObject(object var1, object var2, object var4, object5);
public final native boolean compareAndSwapInt(object var1, object var2, object var4, object5);
public final native boolean compareAndSwapLong(object var1, long var2,long var4m long var6);
```

原子类依赖于CAS思想，但是CAS具体实现是依赖于unsafe对象。

### 2.2 CAS底层原理与unsafe类结合

#### 2.2.1 Unsafe

![image-20230401104411932](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401104411932.png)

​	是CAS的核心类，由于JAVA方法无法直接访问底层系统，需要通过（native）方法来访问，Unsafe相当于一个后门，基于该类可以直接操作特定的内存数据。Unsafe类存在于sun.misc包中，其内部方法操作可以像C的指针一样直接操作内存，因为Java中的CAS操作的执行依赖于Unsafe类的方法。

​	注意Unsafe类中所有的方法都是native修饰的，也就是说Unsafe类中的方法都是直接调用操作系统底层资源执行的和相应任务。

#### 2.2.2 变量valueOffset

表示该变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的

```java
public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
}
```

#### 2.2.3 变量value用volatile修饰

保证了多线程之间的内存可见性

#### 2.2.4 用CAS解决i++无原子性线程不安全问题

CAS的全称为Compare-And-Swap，**他是一条CPU并发原语**

它的功能是判断内存某个位置的值是否为与气质，如果是则更改为新的值，这个过程是原子的。

**AtomicInteger类主要是利用CAS(compare and swap)  + volatile + native 方法来保证原子操作，从而便面synchronizd的高开销，执行效率大为提升。**

![image-20230401110715162](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401110715162.png)

![image-20230401110758727](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401110758727.png)

![image-20230401110858343](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401110858343.png)

CAS并发原语体现在JAVA语言中就是sun.misc.Unsafe类中各个方法。调用Unsafe类中的CAS方法，JVM会帮我们实现出**CAS汇编指令**。这是一种完全依赖于硬件的功能，通过它实现了原子操作。再次由于，由于CAS是一种系统原语，原语属于操作系统用语范畴，**是由若干条指令组成的，用于完成某个功能的一个过程，并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。**

#### 2.2.5 CAS之Unsafe类底层汇编源码分析

```java
 public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

        return var5;
 }
```

假设线程A和线程B两个线程同时执行getAndAddInt操作（分别跑在不同CPU上）：

​	1 AtomicIntege里面得value初始值为3，即内存中AtomicInteger的value值为3，根据JMM模型，线程A和线程B各自持有为3的value的副本分别到各自的工作内存中。

​	2 线程A通过getIntVolatile(var1,var2)拿到value值为3，这时线程A被挂起。

​	3 线程B也通过getIntVolatile(var1,var2)方法获取到value值为3，此时线程B**没有被挂起并执行compareAndSwapInt方法比较内存值也为3，成功修改值为4，线程B打完收工，一切OK**.

​	4.这是线程A恢复，执行compareAndSwapInt方法比较，发现自己的手里的值数字3和内存中的数值4不一致，说明该值已经被其他线程抢先一步执行修改过了，那A线程本次修改失败，只能重新读取重来一遍了。

5. 线程A重新获取value值，因为变量value被volatile修饰，所以其他线程对它的修改，线程A总是能够看到，线程A继续执行compareAndSwapInt进行比较替换，直到成功。

![image-20230401210348845](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401210348845.png)

![image-20230401210859798](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401210859798.png)

![image-20230401211014552](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401211014552.png)

​	**CAS是靠硬件实现的从而在硬件层面提升效率，最底层还是交给硬件来保证原子性和可见性，实现方式是基于硬件平台的汇编指令，在intel的CPU中(x86机器上)，使用汇编指令cmpxchg指令。**

​	**核心思想：比较要更新变量的值V和预期值E(compare)，相等才会将V的值设为新值N(swap)如果不相等自旋再来**



### 2.3 原子引用AtomicReference

```java
import java.util.concurrent.atomic.AtomicReference;

public class AtomicRefenceDemo {
    public static void main(String[] args) {
        AtomicReference<User> atomicReference = new AtomicReference<>();

        User z3 = new User("z3",22);
        User li4 = new User("li4", 28);

        atomicReference.set(z3);
        System.out.println(atomicReference.compareAndSet(z3,li4) + "\t" +atomicReference.get().toString());
        System.out.println(atomicReference.compareAndSet(z3,li4) + "\t" +atomicReference.get().toString());
    }
}


class User{
    String userName;
    int age;

    public User(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}
```

![image-20230401212444205](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401212444205.png)

### 2.4 CAS与自旋锁，借鉴CAS思想

#### 2.4.1 自旋锁是什么

CAS是实现自旋锁的基础，CAS利用CPU指令保证了操作的原子性，以达到锁的效果，至于自旋锁，是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去获取锁，当线程发现锁被占用时，会不断判断锁的状态，知道获取。这样的好处是减少线程上下文切换的消耗，缺点是循环消耗CPU。

#### 2.4.2 手写自旋锁

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
/**
 * 题目：实现一个自旋锁，复习CAS思想
 * 自旋锁好处：循环比较获取没有类似wait的阻塞
 *
 * 通过CAS操作完成自旋锁，A线程先进来调用myLock放啊自己持有锁5秒钟，B随后进来发现
 * 当前有线程持有锁，所以只能通过自旋锁等待，知道A释放锁后B随后抢到。
 * */
public class SpinLockDemo {
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void lock(){
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + "\t" +"----come in");
        while (!atomicReference.compareAndSet(null,thread)){

        }
    }

    public void unlock(){
        Thread thread = Thread.currentThread();
        atomicReference.compareAndSet(thread,null);
        System.out.println(Thread.currentThread().getName() + "\t" + "—————————task over");
    }

    public static void main(String[] args) {
        SpinLockDemo spinLockDemo = new SpinLockDemo();

        new Thread(()->{
            spinLockDemo.lock();
            // 暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(5);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            spinLockDemo.unlock();
        },"A").start();

        // 暂停500毫秒，线程A先于B启动
        try {
            TimeUnit.SECONDS.sleep(5);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        new Thread(()->{
            spinLockDemo.lock();
            // 暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(5);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            spinLockDemo.unlock();
        },"B").start();
    }
}
```

![image-20230401224143985](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401224143985.png)

### 2.5 CAS的两个缺点

#### 2.5.1 循环时间开销很大

我们可以看到getAndAddInt方法执行时，有个do while，如果CAS失败，会一直进行尝试，如果CAS长时间一直不成功，可能会给CPU带来很大的开销。

```java
 public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

        return var5;
 }
```

#### 2.5.2 引入ABA问题

CAS会导致”ABA”问题，CAS算法实现了一个重要前提需要取出内存某时刻的数据并在当下时刻比较并且替换，那么在这个时间差会导致数据变化。

**比如一个线程1从内存位置V取出A，这时候另一个线程2也从内存中取出A，并且线程2进行一些操作将值变成了B，然后线程2又将V位置的数据变成了A，这时候线程1进行了CAS操作发现内存中任然是A，预期Ok，然后线程1操作成功。**

**！！！尽管线程1的CAS操作成功了，但是不代表这个过程时没有问题的。于是引入了版本号的机制**

![](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401232925333.png)

- 解决方案：使用AtomicStampedReference入门

  ```java
  import java.util.concurrent.atomic.AtomicStampedReference;
  
  class Book{
      private int id;
      private String bookName;
  
      public Book(){
  
      }
  
      public Book(int id, String bookName) {
          this.id = id;
          this.bookName = bookName;
      }
  
      public int getId() {
          return id;
      }
  
      public String getBookName() {
          return bookName;
      }
  
      public void setId(int id) {
          this.id = id;
      }
  
      public void setBookName(String bookName) {
          this.bookName = bookName;
      }
  
      @Override
      public String toString() {
          return "Book{" +
                  "id=" + id +
                  ", bookName='" + bookName + '\'' +
                  '}';
      }
  }
  
  public class AtomicStampeDemo {
      public static void main(String[] args) {
          Book javaBook = new Book(1,"javaBook");
  
          AtomicStampedReference<Book> stampedReference = new AtomicStampedReference<>(javaBook,1);
  
          System.out.println(stampedReference.getReference() + "\t" + stampedReference.getStamp());
  
          Book mysqlBook = new Book(2, "mysqlBook");
  
          boolean b = stampedReference.compareAndSet(javaBook, mysqlBook, stampedReference.getStamp(), stampedReference.getStamp() + 1);
  
          System.out.println(b + "\t" + stampedReference.getReference() + "\t" + stampedReference.getStamp());
  
          b = stampedReference.compareAndSet(mysqlBook, javaBook, stampedReference.getStamp(), stampedReference.getStamp() + 1);
  
          System.out.println(b + "\t" + stampedReference.getReference() + "\t" + stampedReference.getStamp());
      }
  }
  ```

  ![image-20230401234534811](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230401234534811.png)

- 深度解决ABA问题

  - 不使用AtomicStampedReference

  ```java
  import java.util.concurrent.TimeUnit;
  import java.util.concurrent.atomic.AtomicInteger;
  import java.util.concurrent.atomic.AtomicStampedReference;
  
  public class ABADemo {
      static AtomicInteger atomicInteger = new AtomicInteger(100);
  
      public static void main(String[] args) {
          new Thread(()->{
              atomicInteger.compareAndSet(100,101);
              try {
                  TimeUnit.MICROSECONDS.sleep(5);
              }catch (InterruptedException e){
                  e.printStackTrace();
              }
              atomicInteger.compareAndSet(101,100);
          },"t1").start();
  
          new Thread(()->{
              try {
                  TimeUnit.MICROSECONDS.sleep(2000);
              }catch (InterruptedException e){
                  e.printStackTrace();
              }
              // 很乐观以为自己原值没有发生改变
              System.out.println(atomicInteger.compareAndSet(100,2022) + "\t" + atomicInteger.get());
          },"t2").start();
      }
  }
  ```

  ![image-20230402001503107](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230402001503107.png)

  - 使用AtomicStampedReference

  ```java
  import java.util.concurrent.TimeUnit;
  import java.util.concurrent.atomic.AtomicInteger;
  import java.util.concurrent.atomic.AtomicStampedReference;
  
  public class ABADemo {
      static AtomicInteger atomicInteger = new AtomicInteger(100);
      static AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<>(100,1);
  
      public static void main(String[] args) {
          new Thread(()->{
              int stamp = stampedReference.getStamp();
              System.out.println(Thread.currentThread().getName() + "\t" + "首次版本号" + stamp);
              try {
                  TimeUnit.MICROSECONDS.sleep(500);
              }catch (InterruptedException e){
                  e.printStackTrace();
              }
              stampedReference.compareAndSet(100,101,stampedReference.getStamp(),stampedReference.getStamp() + 1);
              System.out.println(Thread.currentThread().getName() + "两次流水号" + stampedReference.getStamp());
  
              stampedReference.compareAndSet(101,100,stampedReference.getStamp(),stampedReference.getStamp() + 1);
              System.out.println(Thread.currentThread().getName() + "两次流水号" + stampedReference.getStamp());
          },"t1").start();
  
          new Thread(()->{
              int stamp = stampedReference.getStamp();
              System.out.println(Thread.currentThread().getName() + "\t" + "首次版本号" + stamp);
              // 暂停1秒钟线程，等待上面的t3线程，发生了ABA问题
              try {
                  TimeUnit.MICROSECONDS.sleep(1000);
              }catch (InterruptedException e){
                  e.printStackTrace();
              }
  
              boolean b = stampedReference.compareAndSet(100, 2022, stampedReference.getStamp(), stampedReference.getStamp() + 1);
  
              System.out.println(b + "\t" + stampedReference.getReference() + "\t" + stampedReference.getStamp());
  
          },"t2").start();
      }
  }
  ```

  ![image-20230402000832188](https://raw.githubusercontent.com/bigshcool/myPic/main/image-20230402000832188.png)
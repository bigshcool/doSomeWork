# Stream API

# 1 为什么使用Stream API

- 实际开发层面，项目中多数数据源都来自于MySQL，Oracle等。但是现在数据源可以更多了，有MongDB，Redis等，而这些NoSQL的数据就需要java层面去处理
- Stream和Collection集合区别，**Collection是一种静态的内存数据结构，而Stream是有关计算的。前者是主要面向内存的，存储在内存中，厚泽主要是面向CPU,通过CPU实现计算的**

## 2. 什么是Stream

是数据渠道，用于操作数据源(集合或者数组)所生成的元素序列，**集合讲的是数据，Stream讲的是计算**

- Stream**自己不会存储元素**

- Stream**不会改变源对象**，相反，他们会返回一个持有结果的新Stream

- Stream操作是**延迟操作**，这意味着他们会等到需要结果的时候才会执行

  - Stream的操作步骤

    1. 创建Stream：一个数据源(如:集合、数组)，获取一个流
    2. 中间操作:一个中间操作链，对数据源的数据进行处理
    3. 终止操作（终端操作）：一旦执行终止操作，就**执行中间操作链**，并产生结果，以后，不会再使用

    **延迟的意思就是说调用了终止操作以后，中间操作如过滤、映射才会执行中间操作,同时一旦调用了终止操作，那么就不再执行**

## 3. Stream

工具代码如下

```java
public class EmployeeData {
    public static List<Employee> getEmployees(){
        List<Employee> list = new ArrayList<>();

        list.add(new Employee(1001,"马化腾",34,6000.38));
        list.add(new Employee(1002,"马云",34,4444.38));
        list.add(new Employee(1003,"刘强东",34,3400.38));

        return list;
    }
}
```

### 3.1 通过集合创建

```java
//default Stream<E> stream(); 返回一个顺序流
//default Stream<E> parallelStream():返回一个并行流
    @Test
    public void test01(){
        List<Employee> employees = EmployeeData.getEmployees();

        Stream<Employee> stream = employees.stream();

        Stream<Employee> employeeStream = employees.parallelStream();
    }
```

### 3.2 通过数组创建

```java
// 通过数组
// 调用Arrays类的static <T> Stream<T> stream(T[] array):返回一个流
    @Test
    public void test02(){
        // 通过int数组
        int[] arr = new int[]{1,2,3,4,5};
        IntStream stream = Arrays.stream(arr);
        
        Employee e1 = new Employee(1001,"tom");
        Employee e2 = new Employee(1002,"mike");
        // 自定义数组
        Employee[] arr1 = new Employee[]{e1,e2};
        Stream<Employee> stream1 = Arrays.stream(arr1);
    }
```

### 3.3 通过Stream的of()

```java
    @Test
    public void test3(){
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6);
    }
```

### 3.4 创建无限流(不常用)

```java
// 创建Stream方式四:创建无限流
// 迭代
// public static<T> Stream<T> iterate<final T seed, final UnaryOperator<T> f>
// 生成
// public static<T> Stream<T> generate<Supplier<T> s>
    @Test
    public void test4(){
        Stream.iterate(0, t -> t + 2).limit(10).forEach(System.out::println);

        Stream.generate(Math::random).limit(10).forEach(System.out::println);
    }
```

## 4. Stream操作-中间操作

多个**中间操作**可以连接起来形成一个**流水线**，除非流水线上触发终止操作，否则**中间操作不会执行任何的处理而在终止操作时一次性全部处理，成为"惰性求值"**

- 筛选与切片

  | 方法                | 描述                                                         |
  | ------------------- | ------------------------------------------------------------ |
  | filter(Predicate p) | 接受Lambda，从流中排除某些元素                               |
  | distinct()          | 筛选，通过hashset和equals来判断                              |
  | limit(long maxSize) | 截断流，使其元素不超过给定数量                               |
  | skip()              | 跳过元素，返回一个人掉了前n个元素的流。若流中元素不足n个，则返回一个空留。与limit互补。 |

  ```java
      @Test
      public void test5(){
          // 从流中过滤某些元素
          List<Employee> employees = EmployeeData.getEmployees();
          Stream<Employee> stream = employees.stream();
          // 过滤工资小于4000的
          stream.filter(employee -> employee.getSalary() > 4000).forEach(System.out::println);
          // 截断流 要重新获取流，不然会报错
          employees.stream().limit(2).forEach(System.out::println);
          // 跳过数据
          employees.stream().skip(1).forEach(System.out::println);
      }
  ```

  

- 映射

  | 方法                             | 描述                                                         |
  | -------------------------------- | ------------------------------------------------------------ |
  | **map(Function f)**              | 接受一个函数作为参数，该函数会应用到每个元素上，并将其映射成一个新元素 |
  | mapToDouble(ToDoubleFunction  f) | 接受一个函数作为参数，改函数会被应用到每个元素上，产生了一个新的DoubleStream。 |
  | mapToInt（TointFunction f）      | 接受一个函数作为参数，改函数会被应用到每个元素上，产生了一个新的IntStream。 |
  | mapToLong(ToLongFunction f)      | 接受一个函数作为参数，改函数会被应用到每个元素上，产生了一个新的LongStream. |
  | **flatMap(Function f)**          | 接受一个函数作为参数，将流中的每个值都换成另外一个流，然后把所有的流都连接成一个流。 |

  ```java
      @Test
      public void test6(){
          // map 接受一个函数作为参数，将元素转化成其他形式或者提取信息，该函数会被每一个元素执行
          List<String> list = Arrays.asList("aa","bb","cc","dd");
          list.stream().map(s -> s.toUpperCase()).forEach(System.out::println);
          // 获取员工姓名长度大于3
          List<Employee> employees = EmployeeData.getEmployees();
          Stream<String> nameStream = employees.stream().map(employee -> employee.getName());
          nameStream.filter(name -> name.length() >2).forEach(System.out::println);
          // flatMap(Function f)-接受一个函数作为参数，将流中的每个值都换成另外一个流，然后将他们拼接起来
          // [1,2,3,4] [2,3,4] -> [1,2,3,4,[2,3,4]]
          //                   -> [1,2,3,4,2,3,4]
          Stream<Stream<Character>> streamStream = list.stream().map(StreamAPITest::fromStringToStream);
          streamStream.forEach(s -> {
              s.forEach(System.out::println);
          });
  
          Stream<Character> characterStream = list.stream().flatMap(StreamAPITest::fromStringToStream);
          characterStream.forEach(System.out::println);
      }
  
      public static Stream<Character> fromStringToStream(String str){
          ArrayList<Character> list = new ArrayList<>();
          for (Character c : str.toCharArray()){
              list.add(c);
          }
          return list.stream();
      }
  ```

- 排序

  | 方法                   | 描述                               |
  | ---------------------- | ---------------------------------- |
  | sorted()               | 产生一个新流，其中按自然顺序排序   |
  | sorted(Comparator com) | 产生一个新流，其中按比较器顺序排序 |

  ```java
  @Test
  public void test07(){
      // sorted()-自然排序
      List<Integer> list = Arrays.asList(12,32,42,5,52,6,4,54,643);
      list.stream().sorted().forEach(System.out::println);
      // 非自然排序
      List<Employee> employees = EmployeeData.getEmployees();
      employees.stream().sorted((o1,o2)->{
          int compare = Integer.compare(o1.getAge(), o2.getAge());
          if(compare!=0) return compare;
          return -Double.compare(o1.getSalary(), o2.getSalary());
      }).forEach(System.out::println);
  }
  ```
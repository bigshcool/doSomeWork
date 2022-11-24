package java02;

import org.junit.Test;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 1. 当传递给Lambda体的操作，已经有实现的方法了，可以使用方法引用
 * 2. 方法引用可以看作时Lambda表达式的深层表示。换句话说，方法引用就是Lambda表达式，
 *    也就是函数时接口的一个实例，通过方法的名字来指向一个方法，可以认为是Lambda表达式的一个语法糖
 *    - 要求：实现接口的抽象方法的参数列表和返回值类型，必须与方法引用的方法的参数列表和返回值保持一直！
 * 3. 使用操作符”::“将类（或对象）与方法名分隔开来。
 * 如下三种主要使用情况:
 *    - 对象::实例方法名
 *    - 类::实例静态方法
 *    - 类::实例方法名
 * 4. 方法引用使用的要求:要求接口中的抽象方法中的形参列表和返回值类型与方法引用的方法相同
 */
public class Lambda03_MethodReferences {

    // 情况一:对象::实例方法
    // Consumer中的void accept(T t)
    // PrintStream中的Void Println(T t)
    @Test
    public void test1(){
        // lam表达式的写法
        Consumer<String> consumer =str -> System.out.println(str);
        consumer.accept("北京");
        // 方法引用也是函数接口接口的实例
        PrintStream ps = System.out;
        Consumer<String> consumer1 = ps::println;
        consumer1.accept("南京");
    }

    //Supplier中的T get()
    //Employee中的String getName()
    @Test
    public void test2(){
        Employee employee = new Employee(1004,"王兴",10,6888);
        Supplier<String>  supplier = () -> {
            return employee.getName();
        };
        System.out.println(supplier.get());

        supplier = employee::getName;
        System.out.println(supplier.get());;
    }

    // 情况二：类 :: 静态方法
    // Comparator中的int compare(T t1, T t2)
    // Integer中的int compare(T t1, T t2)
    @Test
    public void test3(){
        Comparator<Integer> com1 = (t1, t2) -> Integer.compare(t1, t2);
        System.out.println(com1.compare(12, 21));

        Comparator<Integer> com2 = Integer::compare;
        System.out.println(com2.compare(12,21));
    }


    // Function中 R apply(T t)
    // Math中的Long round(Double d)
    @Test
    public void test4(){
        Function<Double, Long> fun1 = d ->{
          return Math.round(d);
        };

        System.out.println(fun1.apply(15.666));;

        System.out.println("*********************");

        Function<Double, Long> fun2 = Math::round;

        System.out.println(fun2.apply(15.666));;
    }

    // 情况三: 类::实例方法
    // Comparator中的int comapre(T t1, T t2)
    // String中的int t1.compareTo(t2)
    @Test
    public void test5(){
        Comparator<String> com1 = (s1, s2) -> s1.compareTo(s2);

        System.out.println(com1.compare("abc","abd"));

        System.out.println("*********************");

        Comparator<String> com2 = String::compareTo;

        System.out.println(com2.compare("abc","abd"));
    }

    // BipPredicate中的boolean test(T t1, T t2);
    // String中int t1.equals(t2)
    @Test
    public void test6(){
        BiPredicate<String,String> com1 = (s1, s2) -> s1.equals(s2);

        System.out.println(com1.test("abc","abc"));

        System.out.println("**************************");

        BiPredicate<String, String> pre2 = String ::equals;

        System.out.println(pre2.test("ab","abc"));
    }

    // BigPredicate中的boolean test(T t1, T t2);
    // String中的boolean t1.equals(t2)


}

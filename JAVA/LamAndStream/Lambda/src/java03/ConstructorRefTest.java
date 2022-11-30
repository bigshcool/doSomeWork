package java03;

import java02.Employee;
import org.junit.Test;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 一、构造器引用
 *      和方法引用类似，函数时接口的抽象方法的形参列表和构造器的形参列表一致
 *      抽象方法的返回值几位构造器所属类的类型
 * 二、数组引用
 */

public class ConstructorRefTest {
    // 构造器引用
    // suppelier中的T get()
    @Test
    public void test01(){
        Supplier<Employee> supplier = () -> new Employee(10001,"雷军",23,6000);
        System.out.println(supplier.get());

        System.out.println("************************");

        // 空参构造器
        Supplier<Employee> supplier1 = Employee::new;
        System.out.println(supplier1.get());
    }

    // Function中的R apply(T t)
    @Test
    public void test02(){
        Function<Integer,Employee> function = id -> new Employee(id);
        System.out.println(function.apply(1));

        System.out.println("************************");

        Function<Integer,Employee> function1 = Employee::new;
        System.out.println(function1.apply(1000));
    }

    @Test
    public void test(){
        BiFunction<Integer,String, Employee> biFunction = (id,name)->new Employee(id,name);
        System.out.println(biFunction.apply(1001,"马云"));

        System.out.println("************************");

        BiFunction<Integer,String, Employee> biFunction1 = Employee::new;
        System.out.println(biFunction1.apply(1002,"刘强东"));;
    }
    // 数组引用
    // Function中的R apply(T t)
    @Test
    public void test04(){
        Function<Integer, String[]> func1 = length -> new String[length];

        String[] arr1 = func1.apply(5);

        System.out.println(Arrays.toString(arr1));

        Function<Integer, String[]> func2 = String[] :: new;

        String[] arr2 = func1.apply(5);

        System.out.println(Arrays.toString(arr2));


    }
}

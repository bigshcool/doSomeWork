package java01;

import org.junit.Test;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * 1. 举例: (o1, o2) -> Integer.compare(o1,o2)
 * 2. 格式:
 *      ->: lambda操作符 或者 箭头操作符
 *      ->左边: lambda形参列表(其实就是接口中的抽象方法的形参列表)
 *      ->右边: lambda体(其实就是重写的抽象方法的方法体)
 * 3.Lambda表达式的使用 (分成6种情况)
 *      总结：
 *      ->左边:lambda形参列表的参数类型可以省略，如果lambda参数列表只有一个参数，其一对()也可以省略
 *      ->右边:lambda应该使用一对大括号，如果lamda体只有一条执行语句(可能是return语句)，则省略这一对大括号与return
 *
 * 4.Lambda表达式的本质:
 *                  作为函数式接口的实例
 *
 * 5. 如果一个接口中，只声明了一个抽象方法，则此接口就成为函数式接口，需要使用 @FunctionalInterface注解
 *    @FunctionalInterface
 *    public interface Runnable {
 *
 *      * When an object implementing interface <code>Runnable</code> is used
 *      * to create a thread, starting the thread causes the object's
 *      * <code>run</code> method to be called in that separately executing
 *      * thread.
 *      * <p>
 *      * The general contract of the method <code>run</code> is that it may
 *      * take any action whatsoever.
 *      *
 *      * @see     java.lang.Thread#run()
 *
 *      public abstract void run();
 *   }
 * 6. 目前使用匿名实现类表示的现在都可以使用Lambda表达式来写。
 *
 *
 */
public class Lambda01_BaseUse {
    // 语法格式一: 无参，无返回值
    @Test
    public void test1(){
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("我爱北京天安门");
            }
        };
        r1.run();

        Runnable r2 = () -> System.out.println("我爱北京天安门");
        r2.run();
    }

    @Test
    // 语法格式二:Lambda需要一个参数，但是没有返回值
    public void test2(){
        Consumer<String> con1 = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        };

        con1.accept("我爱北京天安门");

        Consumer<String> con2 = (String s) -> {
            System.out.println(s);
        };

        con2.accept("我爱北京天安门");
    }

    @Test
    // 语法格式三:Lambda：参数类型可以省略，因为可由编译推断得出，成为”类型推断“
    public void test3(){
        Consumer<String> con1 = (String s) -> {
            System.out.println(s);
        };

        con1.accept("我爱北京天安门");

        Consumer<String> con2 = (s) -> {
            System.out.println(s);
        };

        con2.accept("我爱北京天安门");
    }

    @Test
    // 语法格式四:Lambda 若只需一个参数时，参数的小括号可以省略
    public void test4(){
        Consumer<String> con1 = (s) -> {
            System.out.println(s);
        };

        con1.accept("我爱北京天安门");

        Consumer<String> con2 = s -> {
            System.out.println(s);
        };

        con2.accept("我爱北京天安门");
    }

    @Test
    // 无语格式五:Lambda 需要多个参数，多条执行语句，并且可以有返回值
    public void test5(){
        Comparator<Integer> com1 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                System.out.println(o1);
                System.out.println(o2);
                return o1.compareTo(o2);
            }
        };

        System.out.println(com1.compare(12, 31));

        Comparator<Integer> com2 = (o1, o2) -> {
            System.out.println(o1);
            System.out.println(o2);
            return o1.compareTo(o2);
        };

        System.out.println(com2.compare(12, 31));
    }

    @Test
    // 无语格式六:如果Lambda体只有一条语句时，return与大括号若有,则都可以省略
    public void test6(){
        Comparator<Integer> com1 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };

        System.out.println(com1.compare(12, 31));

        Comparator<Integer> com2 = (o1, o2) -> o1.compareTo(o2);

        System.out.println(com2.compare(12, 31));
    }

}

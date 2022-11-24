package java01;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * java内置的4大核心函数式接口
 *
 * 消费型接口 Consumer<T> void accept(T t)
 * 供给型接口 Supplier<T> T get()
 * 函数型接口 Function<T,R> R apply(T t)
 * 断定型接口 Predicate<T> boolean test(T t)
 */
public class Lambda02_InnerCoreFunction {

    @Test
    public void test1(){
       happyTime(500,money -> System.out.println("好好学习"));
    }


    public void happyTime(double money, Consumer<Double> consumer){
        consumer.accept(money);
    }

    @Test
    public void test2(){
        List<String> list = Arrays.asList("北京", "南京" ,"湖北");
        List<String> result = filterString(list, s -> {
            if (s.contains("京")) {
                return true;
            }
            return false;
        });
        System.out.println(result);
    }

    // 根据给定规则，过滤集合中的字符串，此规则有Predicate的方法决定
    public List<String> filterString(List<String> list, Predicate<String> pre){
        ArrayList<String> filterList = new ArrayList<>();
        for(String s : list){
            if (pre.test(s)){
                filterList.add(s);
            }
        }
        return filterList;
    }
}

package java02;

import java.util.ArrayList;
import java.util.List;

public class EmployeeData {
    public static List<Employee> getEmployees(){
        List<Employee> list = new ArrayList<>();

        list.add(new Employee(1001,"马化腾",34,6000.38));
        list.add(new Employee(1002,"马云",34,4444.38));
        list.add(new Employee(1003,"刘强东",34,3400.38));

        return list;
    }
}

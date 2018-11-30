package hx.http.client;

import java.util.LinkedHashSet;

/**
 * @author andy
 * @date 2018-09-04
 */
public class Test {

    private String name;
    private Integer age;


    public Test(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public static void main(String[] args) {
        //1 创建一个LinkedHashSet集合
        LinkedHashSet<String> lhs = new LinkedHashSet<String>();
        lhs.add("ss");
        lhs.add("ss");
        System.out.println(lhs.size());
    }
}

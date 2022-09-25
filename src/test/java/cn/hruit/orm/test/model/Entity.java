package cn.hruit.orm.test.model;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/31 21:21
 **/
public class Entity {
    private String id;
    private String name;
    private int age;
    private boolean sex;

    public Entity() {
    }

    public Entity(boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}

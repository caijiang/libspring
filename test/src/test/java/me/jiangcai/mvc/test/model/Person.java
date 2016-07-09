package me.jiangcai.mvc.test.model;

import java.util.Objects;

/**
 * @author CJ
 */
public class Person {

    private String name;
    private Love love;

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", love=" + love +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) &&
                Objects.equals(love, person.love);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, love);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Love getLove() {
        return love;
    }

    public void setLove(Love love) {
        this.love = love;
    }
}

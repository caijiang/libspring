package me.jiangcai.mvc.test.model;

import java.util.Objects;

/**
 * @author CJ
 */
public class Love {

    @Override
    public String toString() {
        return "Love{" +
                "who='" + who + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Love)) return false;
        Love love = (Love) o;
        return Objects.equals(who, love.who);
    }

    @Override
    public int hashCode() {
        return Objects.hash(who);
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    private String who;
}

package me.jiangcai.lib.sys.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

/**
 * 用于系统保存信息
 * 最多可以保存的长度为255
 *
 * @author CJ
 * @since 3.0
 */
@Entity
public class SystemString {
    @Id
    @Column(length = 50)
    private String id;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemString)) return false;
        SystemString that = (SystemString) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SystemString{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

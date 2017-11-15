package me.jiangcai.poi.template.test.repository;

import me.jiangcai.poi.template.test.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
}

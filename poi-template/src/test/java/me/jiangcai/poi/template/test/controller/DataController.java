package me.jiangcai.poi.template.test.controller;

import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.poi.template.crud.ExeclDramatizer;
import me.jiangcai.poi.template.crud.ExeclReport;
import me.jiangcai.poi.template.test.entity.Message;
import me.jiangcai.poi.template.test.repository.MessageRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author CJ
 */
@Controller
public class DataController {
    @Autowired
    private MessageRepository messageRepository;

    @PostConstruct
    public void init() {
        // 增加几个数据
        int count = 10;
        while (count-- > 0) {
            Message message = new Message();
            message.setValue1(RandomStringUtils.randomAlphabetic(5));
            message.setValue2(RandomStringUtils.randomAlphabetic(5));
            message.setValue3(RandomStringUtils.randomAlphabetic(5));
            message.setValue4(RandomStringUtils.randomAlphabetic(5));
            message.setValue5(RandomStringUtils.randomAlphabetic(5));
            message.setValue6(RandomStringUtils.randomAlphabetic(5));
            message.setValue7(RandomStringUtils.randomAlphabetic(5));
            messageRepository.save(message);
        }
    }

    @GetMapping("/message")
    @RowCustom(distinct = true, dramatizer = ExeclDramatizer.class)
    @ExeclReport("classpath:/message.xml")
    public RowDefinition<Message> message() {
        return new RowDefinition<Message>() {
            @Override
            public String getName() {
                return "无敌者";
            }

            @Override
            public Class<Message> entityClass() {
                return Message.class;
            }

            @Override
            public List<FieldDefinition<Message>> fields() {
                return null;
            }

            @Override
            public Specification<Message> specification() {
                return null;
            }
        };
    }

}

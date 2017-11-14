package me.jiangcai.poi.template;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.poi.template.service.POITemplateServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.function.BiFunction;

/**
 * @author CJ
 */
@ContextConfiguration(classes = POITemplateConfig.class)
public class POITemplateServiceTest extends SpringWebTest {

    @Autowired
    private POITemplateService poiTemplateService;

    @Test
    public void lcm() {
        System.out.println(POITemplateServiceImpl.lcm(1, 1));
        System.out.println(POITemplateServiceImpl.lcm(1, 2));
        System.out.println(POITemplateServiceImpl.lcm(2, 4));
        System.out.println(POITemplateServiceImpl.lcm(3, 6));
        System.out.println(POITemplateServiceImpl.lcm(3, 5));
    }

    @Test
    public void export() throws Exception {
        System.out.println(poiTemplateService);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);

        JsonNode list = objectMapper.readTree(new ClassPathResource("/list.json").getFile());
        File targetFile = new File("target/report.xls");
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            poiTemplateService.export(outputStream, new BiFunction<Integer, Integer, Iterable<?>>() {
                @Override
                public Iterable<?> apply(Integer integer, Integer integer2) {
                    if (integer == 0)
                        return list;
                    return Collections.emptyList();
                }
            }, new ClassPathResource("/demo1.xml"), null);

            outputStream.flush();
        }


        Desktop.getDesktop().open(targetFile);


    }

}
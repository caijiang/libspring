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
import org.springframework.util.StreamUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

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
//        exportOne("demo1");
        exportOne("demo2");
    }

    private void exportOne(String name) throws IOException, IllegalTemplateException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);

        JsonNode list = objectMapper.readTree(new ClassPathResource("/"+name+".json").getFile());
        File targetFile = new File("target/"+name+".xls");
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            poiTemplateService.export(outputStream, (integer, integer2) -> {
                if (integer == 0)
                    return list;
                return Collections.emptyList();
            }, new ClassPathResource("/"+name+".xml"), null);

            outputStream.flush();
        }

        String result = StreamUtils.copyToString(new FileInputStream(targetFile), Charset.forName("UTF-8"));
        System.out.println(result);

        if (Desktop.isDesktopSupported())
            Desktop.getDesktop().open(targetFile);
    }

}
package me.jiangcai.mvc.test.bean;

import me.jiangcai.mvc.test.TypeTest;
import me.jiangcai.mvc.test.model.Love;
import me.jiangcai.mvc.test.model.Person;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author CJ
 */
@Controller
@RequestMapping(TypeTest.URI)
public class LoveTypeController {

    @RequestMapping(value = "/form", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void formPut(Person person, Love love) { // ,Love[] loves
        System.out.println(person);
    }

    @RequestMapping(value = "/json", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void jsonPut(Person person, Love love) { // ,Love[] loves
        System.out.println(person);
    }

//    @InitBinder
//    public void abc(WebDataBinder dataBinder) {
////        dataBinder
//        System.out.println(dataBinder);
////        dataBinder.addc
////        CustomDateEditor customDateEditor;
//        dataBinder.registerCustomEditor(Love.class, new LoveEditor());
//    }
}

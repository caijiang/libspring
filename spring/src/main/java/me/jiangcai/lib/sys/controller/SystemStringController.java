package me.jiangcai.lib.sys.controller;

import me.jiangcai.lib.sys.SystemStringConfig;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + SystemStringConfig.MANAGER_ROLE + "')")
public class SystemStringController {

    @Autowired
    private Environment environment;
    @Autowired
    private SystemStringService systemStringService;

    @GetMapping("${me.jiangcai.lib.sys.uri}")
    @Transactional(readOnly = true)
    public String index(Model model) {
        model.addAttribute("uri", environment.getRequiredProperty("me.jiangcai.lib.sys.uri"));
        model.addAttribute("list", systemStringService.listCustom());
        return "inner.sys";
    }

    @DeleteMapping("${me.jiangcai.lib.sys.uri}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(String id) {
        systemStringService.delete(id);
    }

    @PutMapping("${me.jiangcai.lib.sys.uri}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void put(String id, @RequestBody String value) {
        systemStringService.updateSystemString(id, value);
    }
}

package me.jiangcai.lib.git.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author CJ
 */
@Controller
public class GitRepositoryStateController {
    private final GitRepositoryState state;

    @Autowired
    public GitRepositoryStateController(GitRepositoryState gitRepositoryInformation) {
        this.state = gitRepositoryInformation;
    }

    @RequestMapping("/_version")
    public ResponseEntity<String> version() throws JsonProcessingException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(new ObjectMapper().writeValueAsString(state));
    }
}

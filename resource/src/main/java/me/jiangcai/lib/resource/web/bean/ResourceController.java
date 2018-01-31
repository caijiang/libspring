package me.jiangcai.lib.resource.web.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.lib.resource.Resource;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.seext.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 资源控制器
 * 上传资源分为2种
 * 一种是上传之后再提交给服务器的；这类资源可以在短时间内不被访问即可删除；我们将其放置到/tmp目录
 * 一种是上传之后不经过提交而直接使用的；这类资源我们放置到/watch 目录 只有长时间没有被访问 才可被移除。
 *
 * @author CJ
 */
@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping("/_resourceUpload")
public class ResourceController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ResourceService resourceService;

    /**
     * 为tiny mce 专门设计的图片上传者
     * <a href="https://www.tinymce.com/docs/get-started/upload-images/">More</a>
     *
     * @param file 所上传的资源
     * @throws JsonProcessingException 通常不会发生
     */
    @RequestMapping(value = "/tinyImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> tinyUpload(MultipartFile file) throws JsonProcessingException {
        try {
            Resource resource = uploadWatchedResource(file);
            HashMap<String, Object> body = new HashMap<>();
            body.put("location", resource.httpUrl().toString());
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(objectMapper.writeValueAsString(body));
        } catch (Exception ex) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("error", ex.getLocalizedMessage());
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(objectMapper.writeValueAsString(body));
        }
    }

    /**
     * 为ckeditor而专门设置的编辑上传图片工具
     * 这里有一个很大的问题是上传以后仅仅是告知了客户端一个URL,客户端后来是否删除了 一概不得而知,所以需要一个监视的第三方工具
     * ,如果一直没有对此资源的GET请求,那么这些资源应该被删除。
     *
     * @param upload 所上传的资源
     */
    @RequestMapping(value = "/ckeditorImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> ckeditorUpload(MultipartFile upload) throws JsonProcessingException {
        try {
            Resource resource = uploadWatchedResource(upload);
            HashMap<String, Object> body = new HashMap<>();
            body.put("uploaded", 1);
            body.put("success", true);
            body.put("fileName", resource.getResourcePath());
            body.put("newUuid", resource.getResourcePath());
            body.put("url", resource.httpUrl().toString());
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(objectMapper.writeValueAsString(body));
        } catch (Exception ex) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("uploaded", 0);
            body.put("success", false);
            HashMap<String, Object> error = new HashMap<>();
            error.put("message", ex.getLocalizedMessage());

            body.put("error", error);
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(objectMapper.writeValueAsString(body));
        }
    }

    /**
     * 上传任何资源
     * 将响应201以文本形式告知path，同时在Location头信息上放置URL
     *
     * @param file 所上传的资源
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> upload(MultipartFile file) throws IOException, URISyntaxException {
        Resource resource = uploadTempResource(file);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        httpHeaders.setLocation(resource.httpUrl().toURI());
        return new ResponseEntity<>(resource.getResourcePath(), httpHeaders, HttpStatus.CREATED);
    }


    /**
     * 为fine-uploader特地准备的上传控制器
     *
     * @param file 所上传的资源
     */
    @RequestMapping(value = "/fineUpload", method = RequestMethod.POST)
    @ResponseBody
    public Object fineUpload(MultipartFile file) {
        try {
            Resource resource = uploadTempResource(file);
            HashMap<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("newUuid", resource.getResourcePath());
            return body;
        } catch (Exception ex) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("error", ex.getLocalizedMessage());
            return body;
        }
    }

    @RequestMapping(value = "/jQueryFileUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object jQueryFileUpLoad(MultipartFile file) {
        HashMap<String, Object> body = new HashMap<>();
        try {
            Resource resource = uploadTempResource(file);
            body.put("path", resource.getResourcePath());
            body.put("url", resource.httpUrl().toString());
            body.put("thumbnail_url", resource.httpUrl().toString());
            body.put("name", file.getOriginalFilename());
            body.put("type", getFileNameSuffix(file));
            body.put("size", file.getSize());
            // delete_url delete 还给不了
        } catch (Exception ex) {
            body.put("success", false);
            body.put("error", ex.getLocalizedMessage());
        }
        return body;
    }

    @RequestMapping(value = "/jQueryFilesUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object jQueryFilesUpload(MultipartHttpServletRequest request) {
        return request.getMultiFileMap().values()
                .stream()
                .flatMap(List::stream)
                .map(this::jQueryFileUpLoad)
                .collect(Collectors.toList());
    }

    // 直接开放式的读取
    @RequestMapping(value = "/paths/{path}", method = RequestMethod.GET)
    public ResponseEntity getResource(HttpServletRequest request) throws IOException, URISyntaxException {
        String path = readPath(request);

        Resource resource = resourceService.getResource(path);
        if (!resource.exists())
            return ResponseEntity.notFound().build();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(resource.httpUrl().toURI())
                .build();
    }

    private String readPath(HttpServletRequest request) throws UnsupportedEncodingException {
        String path = request.getRequestURI().substring("/_resourceUpload/paths/".length());
        while (path.contains("%"))
            path = URLDecoder.decode(path, "UTF-8");
        return path;
    }

    // 同样开放式的删除，但只能删除临时区域的
    @RequestMapping(value = "/paths/{path}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(HttpServletRequest request) throws IOException {
        String path = readPath(request);
        if (!path.startsWith("tmp/") && !path.startsWith("watch/"))
            throw new IllegalArgumentException();
        resourceService.deleteResource(path);
    }


    //这个干嘛用的？？a
//    @RequestMapping(method = RequestMethod.POST, value = "/webUploader")
//    public ResponseEntity<?> webUploader(String id, MultipartFile file) throws IOException, URISyntaxException {
//        // WU_FILE_0
//        // 响应 包括 id 和 url
//        try (InputStream inputStream = file.getInputStream()) {
//            String path = uploadTempResource(inputStream, file);
//            Resource resource = resourceService.getResource(path);
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
//            HashMap<String, String> data = new HashMap<>();
//            data.put("id", path);
//            data.put("url", resource.httpUrl().toString());
//            return new ResponseEntity<>(objectMapper.writeValueAsBytes(data), httpHeaders, HttpStatus.OK);
//        }
//    }

    private Resource uploadTempResource(MultipartFile file) throws IOException {
        return uploadResource("tmp/", file);
    }

    private Resource uploadWatchedResource(MultipartFile file) throws IOException {
        return uploadResource("watch/", file);
    }

    private Resource uploadResource(String pathPrefix, MultipartFile file) throws IOException {
        final String path = pathPrefix + randomFileName(file);
        try (InputStream inputStream = file.getInputStream()) {
            return resourceService.uploadResource(path, inputStream);
        }
    }

    /**
     * @param file 上传的文件
     * @return 根据上传的信息 生成携带有准确后缀的临时名
     */
    private String randomFileName(MultipartFile file) {
        String suffix = getFileNameSuffix(file);
        return UUID.randomUUID().toString().replaceAll("-", "")
                + "." + suffix;
    }

    private String getFileNameSuffix(MultipartFile file) {
        String suffix;
        try {
            suffix = FileUtils.fileExtensionName(file.getOriginalFilename());
        } catch (Exception ignored) {
            suffix = MediaType.parseMediaType(file.getContentType()).getSubtype();
        }
        return suffix;
    }


}

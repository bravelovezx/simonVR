package com.example.simon.controller;

import com.example.simon.entity.TranslateRequest;
import com.example.simon.util.AuthV3Util;
import com.example.simon.util.HttpUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 网易有道智云翻译服务api调用demo
 * api接口: https://openapi.youdao.com/api
 */
@RestController
@RequestMapping("/api/translate")
@Tag(name = "有道翻译", description = "翻译API")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class TranslateController {

    private static final String APP_KEY = "270213796dab3c53";     // 您的应用ID
    private static final String APP_SECRET = "aNQk7obqgNt4s3C1aDak1Ob67DOlYZKH";  // 您的应用密钥

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        return (Integer) details.get("userId");
    }

    @PostMapping("translate")
    public String translate(@RequestBody TranslateRequest params) throws NoSuchAlgorithmException {
        System.out.println("TranslateController.translate: " + params);
        params.setVocabId(getCurrentUserId().toString());
        Map<String, String[]> p = params.ToMap();
        // 添加鉴权相关参数
        AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, p);
        // 请求api服务
        byte[] result = HttpUtil.doPost("https://openapi.youdao.com/api", null, p, "application/json");
        // 打印返回结果
        return new String(result, StandardCharsets.UTF_8);
//        if (result != null) {
//            System.out.println(new String(result, StandardCharsets.UTF_8));
//        }
    }

}

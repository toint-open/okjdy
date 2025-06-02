///*
// * Copyright 2025 Toint (599818663@qq.com)
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package cn.toint.jdy4j.core.service.impl;
//
//import cn.toint.jdy4j.core.model.JdyGetUploadTokenRequest;
//import cn.toint.jdy4j.core.model.JdyGetUploadTokenResponse;
//import cn.toint.jdy4j.core.enums.JdyUrlEnum;
//import cn.toint.jdy4j.core.service.JdyFileService;
//import cn.toint.jdy4j.core.service.JdyRequestService;
//import cn.toint.tool.util.Assert;
//import cn.toint.tool.util.JacksonUtil;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.dromara.hutool.core.collection.CollUtil;
//import org.dromara.hutool.core.io.file.FileUtil;
//import org.dromara.hutool.extra.validation.ValidationUtil;
//import org.dromara.hutool.http.client.Request;
//import org.dromara.hutool.http.client.body.MultipartBody;
//
//import java.io.File;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
///**
// * @author Toint
// * @date 2024/10/19
// */
//@Slf4j
//public class JdyFileServiceImpl implements JdyFileService {
//    /**
//     * 简道云请求
//     */
//    @Resource
//    private JdyRequestService jdyRequestService;
//
//    @Override
//    public List<JdyGetUploadTokenResponse> getUploadToken(final JdyGetUploadTokenRequest getUploadTokenRequest) {
//        // 1.参数校验
//        ValidationUtil.validateAndThrowFirst(getUploadTokenRequest);
//
//        // 2.构造请求对象
//        final Request request = Request.of(JdyUrlEnum.GET_UPLOAD_TOKEN.getUrl())
//                .body(JacksonUtil.writeValueAsString(getUploadTokenRequest))
//                .method(JdyUrlEnum.GET_UPLOAD_TOKEN.getMethod());
//
//        // 3.执行请求
//        final JsonNode response = this.jdyRequestService.request(request);
//
//        // 4.读取响应
//        return JacksonUtil.treeToValue(response.get("token_and_url_list"), new TypeReference<>() {
//        });
//    }
//
//    @Override
//    public String upload(final String token, final File file) {
//        // 1.Assert
//        Assert.notBlank(token, "token must not be blank");
//        Assert.notNull(file, "file must not be null");
//        Assert.isTrue(FileUtil.exists(file), "file must exist");
//
//        // 2.构造请求对象
//        final LinkedHashMap<String, Object> bodyMap = new LinkedHashMap<>();
//        bodyMap.put("token", token);
//        bodyMap.put("file", file); // file 需要作为最后一个参数。
//
//        final Request request = Request.of("JdyUrlEnum.UPLOAD_FILE.getUrl()")
//                .body(MultipartBody.of(bodyMap, StandardCharsets.UTF_8))
//                ;
//
//        // 3.执行请求
//        final JsonNode response = this.jdyRequestService.request(request);
//
//        // 4.读取响应
//        return Optional.ofNullable(response.get("key")).map(JsonNode::asText).orElseThrow();
//    }
//
//    @Override
//    public String upload(final JdyGetUploadTokenRequest getUploadTokenRequest, final File file) {
//        Assert.notNull(getUploadTokenRequest, "getUploadTokenRequest must not be null");
//        Assert.notNull(file, "file must not be null");
//        Assert.isTrue(FileUtil.exists(file), "file must exist");
//        return this.uploadBatch(getUploadTokenRequest, List.of(file)).getFirst();
//    }
//
//    @Override
//    public List<String> uploadBatch(final Map<String, File> uploadEntityMap) {
//        Assert.notEmpty(uploadEntityMap, "uploadEntityMap must not be empty");
//
//        final List<String> successFileKeys = new ArrayList<>();
//
//        uploadEntityMap.forEach((token, file) -> {
//            if (StringUtils.isBlank(token) || Objects.isNull(file)) {
//                log.error("文件无法上传已被过滤, 文件路径: {}", FileUtil.getAbsolutePath(file));
//                return;
//            }
//
//            try {
//                final String fileKey = this.upload(token, file);
//                successFileKeys.add(fileKey);
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//            }
//        });
//        return successFileKeys;
//    }
//
//    @Override
//    public List<String> uploadBatch(final JdyGetUploadTokenRequest getUploadTokenRequest, final Collection<File> files) {
//        Assert.notEmpty(files, "files must not be empty");
//
//        // 获取token,不会重复
//        final Set<String> tokens = new HashSet<>();
//        final int count = BigDecimal.valueOf(files.size()).divide(BigDecimal.valueOf(100), 0, RoundingMode.UP).intValue();
//        for (int i = 0; i < count; i++) {
//            tokens.addAll(CollUtil.map(this.getUploadToken(getUploadTokenRequest), JdyGetUploadTokenResponse::getToken));
//        }
//
//        Assert.isTrue(tokens.size() >= files.size(), "获取的 tokens 数量不足以分配要上传的文件, tokensSize: {}, filesSize: {}", tokens.size(), files.size());
//
//        // 用于优雅消耗token
//        final LinkedList<String> tokenLinkedList = new LinkedList<>(tokens);
//
//        // k:token, v:file
//        final Map<String, File> uploadEntityMap = new HashMap<>();
//        files.stream()
//                .filter(Objects::nonNull)
//                .filter(FileUtil::exists)
//                .forEach(file -> uploadEntityMap.put(tokenLinkedList.pop(), file));
//
//        return this.uploadBatch(uploadEntityMap);
//    }
//}

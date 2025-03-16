/*
 * Copyright 2025 Toint (599818663@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.toint.jdy4j.core.service;

import cn.toint.jdy4j.core.model.JdyGetUploadTokenRequest;
import cn.toint.jdy4j.core.model.JdyGetUploadTokenResponse;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 简道云文件
 *
 * @author Toint
 * @date 2024/10/19
 */
public interface JdyFileService {
    /**
     * 获取文件上传凭证和上传地址接口
     */
    List<JdyGetUploadTokenResponse> getUploadToken(JdyGetUploadTokenRequest getUploadTokenRequest);

    /**
     * 文件上传接口
     *
     * @param token 文件上传凭证,调用{@link JdyFileService#getUploadToken}获取
     */
    String upload(String token, File file);

    /**
     * 文件上传接口(无需关心文件上传token,内部会自动获取)
     *
     * @param getUploadTokenRequest getUploadTokenRequest
     * @param file                  文件对象,其中文件上传凭证内部会自动调用{@link JdyFileService#getUploadToken}获取
     */
    String upload(JdyGetUploadTokenRequest getUploadTokenRequest, File file);

    /**
     * 文件批量上传接口
     *
     * @param uploadEntityMap 文件上传凭证与文件对象,其中文件上传凭证调用{@link JdyFileService#getUploadToken}获取
     */
    List<String> uploadBatch(Map<String, File> uploadEntityMap);

    /**
     * 文件批量上传接口(无需关心文件上传token,内部会自动获取)
     *
     * @param getUploadTokenRequest getUploadTokenRequest
     * @param fileCollection        文件对象集合,其中文件上传凭证内部会自动调用{@link JdyFileService#getUploadToken}获取
     */
    List<String> uploadBatch(JdyGetUploadTokenRequest getUploadTokenRequest, Collection<File> fileCollection);
}

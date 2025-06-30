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
package cn.toint.okjdy.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 简道云字段类型
 *
 * @author Toint
 * @date 2024/10/20
 */
@Getter
@AllArgsConstructor
public enum JdyFieldTypeEnum {
    /**
     * 文本
     */
    TEXT("text"),

    /**
     * 多行文本
     */
    TEXT_AREA("textarea"),

    /**
     * 流水号
     */
    SN("sn"),

    /**
     * 数字
     */
    NUMBER("number"),

    /**
     * 日期时间
     */
    DATE_TIME("datetime"),

    /**
     * 单选按钮组
     */
    RADIO_GROUP("radiogroup"),

    /**
     * 复选框组
     */
    CHECK_BOX_GROUP("checkboxgroup"),

    /**
     * 下拉框
     */
    COMBO("combo"),

    /**
     * 下拉复选框
     */
    COMBO_CHECK("combocheck"),

    /**
     * 地址
     */
    ADDRESS("address"),

    /**
     * 定位
     */
    LOCATION("location"),

    /**
     * 图片
     */
    IMAGE("image"),

    /**
     * 附件
     */
    UPLOAD("upload"),

    /**
     * 子表单
     */
    SUBFORM("subform"),

    /*
    选择数据
     */
    LINK_DATA("linkdata"),

    /**
     * 手写签名
     */
    SIGNA_TURE("signature"),

    /**
     * 成员单选
     */
    USER("user"),

    /**
     * 成员多选
     */
    USERG_ROUP("usergroup"),

    /**
     * 部门单选
     */
    DEPT("dept"),

    /**
     * 部门多选
     */
    DEPT_GROUP("deptgroup"),

    /**
     * 手机
     */
    PHONE("phone"),

    /**
     * 关联数据
     */
    LOOKUP("lookup"),

    /**
     * 聚合计算
     */
    AGGREGATION("aggregation");

    private final String value;
}

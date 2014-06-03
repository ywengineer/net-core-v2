/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * JSON.java
 *
 * 2013 2013-6-7 下午1:44:35
 *
 *******************************************************************/
package com.handee.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

/**
 * class description
 *
 * @author Mark
 */
public class JSON {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JSON.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
    }

    /**
     * 将Java对象转换成Json字符串
     *
     * @param src 对象
     * @return JSON字符串
     */
    public static String toJson(Object src) {
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, src);
        } catch (Exception e) {
            LOG.error("write json object to string error", e);
        }
        return writer.toString();
    }

    /**
     * 将Java对象转换成Json字符串
     *
     * @param src 对象
     * @return JSON字符串
     */
    public static String toPrettyJson(Object src) {
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, src);
        } catch (Exception e) {
            LOG.error("write json object to string error", e);
        }
        return writer.toString();
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param json  字符串
     * @param clazz 对象类型
     * @return JSON字符串
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return (T) objectMapper.readValue(json, TypeFactory.rawClass(clazz));
        } catch (Exception e) {
            LOG.error("convert json string to an object[" + clazz.getName() + "] error", e);
        }
        return null;
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param json    字符串
     * @param typeRef 类型
     * @return JSON字符串
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return (T) objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            LOG.error("convert json string to an object[" + typeRef.getType().getClass() + "] error", e);
        }
        return null;
    }

    /**
     * 返回JSON处理器
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}

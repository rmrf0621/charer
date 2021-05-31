package com.sharer.server.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sharer.server.core.distributed.ImNode;
import com.sharer.server.core.vo.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils<T> {

    private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);


    private final static ObjectMapper mapper = new ObjectMapper(); // create once, reuse

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public static String toJSONString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("对象转换json失败,{}",e.getMessage());
            return null;
        }
    }

    public static byte[] toByte(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("对象转换byte失败,{}",e.getMessage());
            return null;
        }
    }

    public static <T> T bytes2Object(byte[] b, Class<T> clazz) {
        try {
            return mapper.readValue(b, clazz);
        } catch (Exception e) {
            logger.error("bytes转换对象失败,{}",e.getMessage());
            return null;
        }
    }

    public static  <T> T json2Object(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("json转换对象失败,{}",e.getMessage());
            return null;
        }
    }


    public static void main(String[] args) throws JsonProcessingException {

    }
}

package com.sharer.server.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharer.server.core.distributed.ImNode;

public class JsonUtils<T> {

    private final static ObjectMapper mapper = new ObjectMapper(); // create once, reuse

//    static {
//        AnnotationIntrospector dimensionFieldSerializer = new DimensionFieldSerializer();
//        mapper.setAnnotationIntrospector(dimensionFieldSerializer);
//    }

    public static String toJSONString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.getStackTrace();
            return null;
        }
    }

    public static byte[] toByte(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.getStackTrace();
            return null;
        }
    }

    public static <T> T bytes2Object(byte[] b, Class<T> clazz) {
        try {
            return mapper.readValue(b, clazz);
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }


//    public static void main(String[] args) {
//        UserVo userVo = new UserVo();
//        userVo.setAccount("1000000");
//        System.out.println(toJSONString(userVo));
//    }
}

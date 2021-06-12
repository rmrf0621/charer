package com.sharer.common.utils;

public class ResultGenerater<T> {



    public static <T> Result success() {
        return new Result(200, "请求成功!");
    }

    public static <T> Result success(T data) {
        return new Result(200, data, "请求成功!");
    }

    public static <T> Result success(Integer code, T data) {
        return new Result(200, data, "请求成功!");
    }

    public static <T> Result fail() {
        return new Result(500, "请求失败!");
    }

    public static <T> Result fail(String message) {
        return new Result(500, "请求失败!");
    }

    public static <T> Result fail(Integer code, String message) {
        return new Result(code, message);
    }
}

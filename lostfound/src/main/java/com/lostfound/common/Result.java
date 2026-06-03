package com.lostfound.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /** 状态码 */
    private int code;

    /** 提示消息 */
    private String msg;

    /** 响应数据 */
    private T data;

    /**
     * 请求成功（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 请求成功（仅消息，无数据）
     */
    public static Result<Void> success(String msg) {
        return new Result<>(200, msg, null);
    }

    /**
     * 请求成功（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 请求成功（自定义消息 + 数据）
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /**
     * 请求失败
     */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 请求失败（默认400）
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(400, msg, null);
    }

    /**
     * 未授权（401）
     */
    public static <T> Result<T> unauthorized(String msg) {
        return new Result<>(401, msg, null);
    }

    /**
     * 禁止访问（403）
     */
    public static <T> Result<T> forbidden(String msg) {
        return new Result<>(403, msg, null);
    }

    /**
     * 资源不存在（404）
     */
    public static <T> Result<T> notFound(String msg) {
        return new Result<>(404, msg, null);
    }

    /**
     * 服务器错误（500）
     */
    public static <T> Result<T> serverError(String msg) {
        return new Result<>(500, msg, null);
    }
}

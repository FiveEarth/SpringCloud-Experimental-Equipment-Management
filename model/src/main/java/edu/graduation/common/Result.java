package edu.graduation.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 全局统一返回结果类
 * 适配所有接口的响应格式，包含状态码、提示消息、业务数据
 */
@Data
@Schema(description = "统一返回结果")
public class Result<T> {

    /**
     * 响应状态码：
     * 200 - 成功
     * 400 - 业务参数错误
     * 500 - 系统异常
     * 其他可自定义（如401未授权、403禁止访问等）
     */
    @Schema(description = "响应状态码")
    private Integer code;

    /**
     * 响应提示消息（成功/失败的描述）
     */
    @Schema(description = "响应提示消息")
    private String msg;

    /**
     * 响应业务数据（成功时返回，失败时为null）
     */
    @Schema(description = "响应业务数据")
    private T data;

    // ======================== 静态工具方法（简化调用） ========================
    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(null);
        return result;
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（自定义消息+数据）
     */
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应（自定义状态码+消息）
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /**
     * 失败响应（默认500状态码）
     */
    public static <T> Result<T> fail(String msg) {
        return fail(500, msg);
    }

    /**
     * 失败响应（参数错误：默认400状态码）
     */
    public static <T> Result<T> paramFail(String msg) {
        return fail(400, msg);
    }
}
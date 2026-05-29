package org.example.domain;

/**
 * 统一返回结果封装类
 * @param <T> 数据类型
 */
public class Result<T> {

    private Integer code;    // 状态码
    private String msg;      // 消息
    private T data;          // 数据

    // 私有构造函数，强制使用静态工厂方法
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 无参构造函数（用于序列化）
    public Result() {
    }

    // Getter 和 Setter 方法
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功返回结果（带数据）
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功返回结果（不带数据）
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 成功返回结果（自定义消息）
     * @param data 返回的数据
     * @param msg 自定义消息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data, String msg) {
        return new Result<>(200, msg, data);
    }

    /**
     * 失败返回结果
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    /**
     * 失败返回结果（自定义状态码）
     * @param code 状态码
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 参数验证失败返回结果
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> validateFailed() {
        return error(400, "参数验证失败");
    }

    /**
     * 未登录返回结果
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> unauthorized() {
        return error(401, "暂未登录或token已经过期");
    }

    /**
     * 未授权返回结果
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> forbidden() {
        return error(403, "没有相关权限");
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
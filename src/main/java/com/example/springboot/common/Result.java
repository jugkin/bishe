package com.example.springboot.common;





public class Result {

    private String code; // 状态码：200 成功，500 系统错误，401 未授权等
    private String msg;   // 提示信息
    private Object data;       // 具体的数据


    public static Result success() {
        Result result = new Result();
        result.setCode("200");
        result.setMsg("请求成功");
        return result;
    }
    public static Result success(Object data) {
        Result result=success();
        result.setData(data);
        return result;
    }
    public static Result error() {
        Result result = new Result();
        result.setCode("500");
        result.setMsg("系统错误");
        return result;
    }
    // 新增：支持只传一个错误信息
    public static Result error(String msg) {
        Result result = new Result();
        result.setCode("500"); // 默认错误码 500
        result.setMsg(msg);    // 使用传入的错误信息
        return result;
    }
    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public Result() {
    }

    public Result(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 获取
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取
     * @return data
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }

    public String toString() {
        return "Result{code = " + code + ", msg = " + msg + ", data = " + data + "}";
    }
}
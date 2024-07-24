package com.holo.enums;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description:
 */
public enum RpcErrorMessage {
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接失败"),
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_CAN_NOT_BE_FOUND("服务未找到"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现任何接口"),
    REQUEST_NOT_MATCH_RESPONSE("返回结果错误！请求与对应的响应不是一对");

    private final String message;

    RpcErrorMessage(String message) {
        this.message = message;
    }

    public String Message() {
        return message;
    }

    @Override
    public String toString() {
        return "RpcErrorMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}

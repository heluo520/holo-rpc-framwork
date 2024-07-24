package com.holo.enums;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description:
 */

public enum StatusCode {
    SUCCESS(200,"OK."),
    FAIL(500,"call is fail.");
    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int Code() {
        return code;
    }

    public String Message() {
        return message;
    }

    @Override
    public String toString() {
        return "StatusCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

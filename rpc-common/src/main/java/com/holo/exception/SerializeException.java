package com.holo.exception;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-23
 * @Description:
 */
public class SerializeException extends RuntimeException{
    public SerializeException() {
    }

    public SerializeException(String message) {
        super(message);
    }
}

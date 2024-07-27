package com.holo.exception;

import com.holo.enums.RpcErrorMessageEnum;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-23
 * @Description:
 */
public class RpcException extends RuntimeException{
    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }
    public RpcException(RpcErrorMessageEnum msg, String detail){
        super(msg.Message()+": "+detail);
    }
    public RpcException(String message,Throwable throwable){
        super(message,throwable);
    }

}

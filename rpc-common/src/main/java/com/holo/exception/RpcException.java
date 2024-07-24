package com.holo.exception;

import com.holo.enums.RpcErrorMessage;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-23
 * @Description:
 */
public class RpcException extends BaseException{
    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }
    public RpcException(RpcErrorMessage msg,String detail){
        super(msg.Message()+": "+detail);
    }

}

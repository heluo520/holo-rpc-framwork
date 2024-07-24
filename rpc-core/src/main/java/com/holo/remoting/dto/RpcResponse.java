package com.holo.remoting.dto;

import com.holo.enums.RpcErrorMessage;
import com.holo.enums.StatusCode;
import lombok.*;

import java.io.Serializable;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 1367041782553351986L;
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 描述信息
     */
    private String message;
    /**
     * 响应消息体
     */
    private T data;

    /**
     * 请求成功响应
     * @param requestId 与请求id对应
     * @param data 响应体内容
     * @return RpcResponse<T> 响应格式
     * @param <T> 响应体类型
     */

    public static <T> RpcResponse<T> success(String requestId,T data){
        RpcResponse<Object> response = RpcResponse.builder()
                .code(StatusCode.SUCCESS.Code())
                .message(StatusCode.SUCCESS.Message())
                .requestId(requestId)
                .data(data)
                .build();
        return (RpcResponse<T>) response;
    }

    /**
     * 请求失败响应
     * @param rpcErrorMessageCode RpcErrorMessage枚举类型的code
     * @param rpcErrorMessageMessage RpcErrorMessage枚举类型的message
     * @return
     * @param <T>
     */
    public static <T> RpcResponse<T> fail(int rpcErrorMessageCode,String rpcErrorMessageMessage){
        RpcResponse<Object> response = RpcResponse.builder()
                .code(rpcErrorMessageCode)
                .message(rpcErrorMessageMessage)
                .build();
        return (RpcResponse<T>) response;
    }

}

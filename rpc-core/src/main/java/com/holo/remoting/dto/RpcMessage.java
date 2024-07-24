package com.holo.remoting.dto;

import lombok.*;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description: 自定义消息格式
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    /**
     * rpc消息类型
     */
    private byte messageType;
    /**
     * 消息编码类型/序列化类型
     */
    private byte codec;
    /**
     * 消息压缩类型
     */
    private byte compress;
    /**
     * rpc请求id
     */
    private int requestId;
    /**
     * 消息内容
     */
    private Object data;

}

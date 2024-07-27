package com.holo.remoting.netty.client;

import com.holo.enums.CompressTypeEnum;
import com.holo.enums.SerializationTypeEnum;
import com.holo.factory.SingletonFactory;
import com.holo.remoting.constants.RpcConstants;
import com.holo.remoting.dto.RpcMessage;
import com.holo.remoting.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description: 客户端消息处理
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<Object> {

    private final UnprocessedRequests unprocessedRequests;
    private final NettyRpcClient client;

    public RpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.client = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        log.info("客户端handler处理消息：{}",o);
        if(o instanceof RpcMessage){
            RpcMessage msg = (RpcMessage) o;
            byte messageType = msg.getMessageType();
            if(messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE){
                log.info("消息为心跳响应：{}",msg.getData());
            }else if(messageType == RpcConstants.RESPONSE_TYPE){
                RpcResponse<Object> response = (RpcResponse<Object>) msg.getData();
                unprocessedRequests.complete(response);
            }
        }
    }
    //处理事件的方法，不止可以处理IO事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.WRITER_IDLE){
                log.info("通道 {} 可以进行写操作",ctx.channel().remoteAddress());
                //执行心跳请求
                Channel channel = client.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = RpcMessage.builder()
                        .codec(SerializationTypeEnum.KYRO.getCode())
                        .compress(CompressTypeEnum.GZIP.getCode())
                        .messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE)
                        .data(RpcConstants.PING).build();
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理请求出现异常：{}",cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}

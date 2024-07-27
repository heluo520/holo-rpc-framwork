package com.holo.remoting.netty.server;

import com.holo.enums.CompressTypeEnum;
import com.holo.enums.RpcResponseCodeEnum;
import com.holo.enums.SerializationTypeEnum;
import com.holo.factory.SingletonFactory;
import com.holo.remoting.constants.RpcConstants;
import com.holo.remoting.dto.RpcMessage;
import com.holo.remoting.dto.RpcRequest;
import com.holo.remoting.dto.RpcResponse;
import com.holo.remoting.handler.TargetMethodHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description:
 */
@Slf4j
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    private final TargetMethodHandler targetMethodHandler;

    public RpcServerHandler() {
        this.targetMethodHandler = SingletonFactory.getInstance(TargetMethodHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if(msg instanceof RpcMessage){
                log.info("服务端接收消息：{}",msg);
                RpcMessage msg1 = (RpcMessage) msg;
                byte messageType = msg1.getMessageType();
                RpcMessage rpcMessage = RpcMessage.builder()
                        .codec(SerializationTypeEnum.KYRO.getCode())
                        .compress(CompressTypeEnum.GZIP.getCode())
                        .build();
                if(messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE){
                    //为心跳类型
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                }else {
                    //为请求类型
                    RpcRequest rpcRequest = (RpcRequest)msg1.getData();
                    //处理请求，即执行对应的方法获取结果
                    Object result = targetMethodHandler.handle(rpcRequest);
                    //封装结果
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    if(ctx.channel().isActive() && ctx.channel().isWritable()){
                        //管道正常可写，构造一个响应message
                        RpcResponse<Object> rpcResponse = RpcResponse.success(rpcRequest.getRequestId(), result);
                        rpcMessage.setData(rpcResponse);
                    }else {
                        RpcResponse<Object> response = RpcResponse.fail(RpcResponseCodeEnum.FAIL.getCode(), RpcResponseCodeEnum.FAIL.getMessage());
                        rpcMessage.setData(response);
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }

        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //自己添加指定事件类型的处理逻辑
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("管道读闲置，关闭管道");
                ctx.close();
            }
        } else {
            //不是希望的事件使用原处理逻辑
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端出现异常：{}",cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}

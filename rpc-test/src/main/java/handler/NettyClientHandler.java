package handler;

import entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-19
 * @Description: 客户端消息处理器，将其绑定到通道的Map上
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RpcResponse response = (RpcResponse) msg;
            log.info("收到服务端回复并绑定到AttributeMap：{}",response);
            //创建一个管道自定义属性的key对象
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            //通过key获得与key关联的属性对象并设置其值
            ctx.channel().attr(key).set(response);
            //关闭通道
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端Handler出错：{}",cause.getMessage());
        cause.printStackTrace();
        //关闭连接管道
        ctx.close();
    }
}

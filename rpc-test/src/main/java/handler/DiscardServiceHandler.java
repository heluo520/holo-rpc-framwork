package handler;

import entity.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-18
 * @Description: 一个响应服务器消息处理类，原样返回消息
 */

public class DiscardServiceHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DiscardServiceHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            /*ByteBuf in = (ByteBuf) msg;
            logger.info("收到消息：{}", in.toString(CharsetUtil.US_ASCII));*/
            ctx.writeAndFlush(msg);
        }finally {

        }
        /*finally {
            ReferenceCountUtil.release(msg);
        }*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //出现异常就关闭连接
        ctx.close();
    }
}

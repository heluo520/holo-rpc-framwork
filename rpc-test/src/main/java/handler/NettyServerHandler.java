package handler;

import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-19
 * @Description: 服务端消息处理器
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 一个具有原子性的整型变量，被当作线程安全计数器
     */
    private static final AtomicInteger atomicInteger = new AtomicInteger(1);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RpcRequest request = (RpcRequest) msg;
            log.info("服务端接收到消息：[{}] 共计接收次数：[{}]",request,atomicInteger.getAndIncrement());
            RpcResponse response = RpcResponse.builder().message("服务端响应").build();
            //将响应数据发送给客户端，并添加一个默认的关闭连接管道的处理
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } finally {
            //销毁对象msg，因为其是一个引用计数对象
            //如果该对象被writeAndFlush则不需要显示释放，Netty会帮我们释放
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端Handler出错：{}",cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}

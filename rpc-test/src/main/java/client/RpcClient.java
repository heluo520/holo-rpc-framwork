package client;

import api.KryoSerializer;
import entity.RpcRequest;
import entity.RpcResponse;
import handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import utils.NettyKryoDecoder;
import utils.NettyKryoEncoder;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-18
 * @Description:
 */
@Slf4j
public class RpcClient {
    private String ip;
    private int port;
    private static final Bootstrap b = new Bootstrap();

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("127.0.0.1", 8888);
        for (int i = 0; i < 4; i++) {
            log.info("=====start======");
            RpcRequest msg = RpcRequest.builder().interfaceName("interface").methodName("method").build();
            RpcResponse response = rpcClient.sendMessage(msg);
            log.info("收到消息：{}",response);
            log.info("======end=====");
        }
    }
    //初始化资源
    static {
        //处理I/O操作的多线程事件循环处理器
        NioEventLoopGroup boss = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(boss)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    //配置新创建的通道
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //配置处理器链pipline
                        //RpcResponse -> ByteBuf
                        socketChannel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        //ByteBuf -> RpcRequest
                        socketChannel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer,RpcRequest.class));
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    public RpcClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public RpcResponse sendMessage(RpcRequest request){
        try {
            ChannelFuture f = b.connect(ip,port).sync();
            log.info("客户端连接：{}:{}",ip,port);
            Channel channel = f.channel();
            if(channel!=null){
                channel.writeAndFlush(request).addListener(future -> {
                    if (future.isSuccess()){
                        log.info("客户端发送消息：[ {} ]",request);
                    }else {
                        log.warn("客户端发送消息失败：{}",future.cause());
                    }
                });
                //阻塞等待，直到channel关闭
                channel.closeFuture().sync();
                //取出管道中保存的相应数据返回
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return channel.attr(key).get();
            }
        } catch (InterruptedException e) {
            log.error("客户端出错：{}",e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}

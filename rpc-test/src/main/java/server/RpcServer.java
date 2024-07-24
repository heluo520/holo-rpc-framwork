package server;

import api.KryoSerializer;
import entity.RpcRequest;
import entity.RpcResponse;
import handler.DiscardServiceHandler;
import handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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
public class RpcServer{
    private int port;

    public static void main(String[] args) {
        new RpcServer(8888).run();
    }

    public RpcServer(int port) {
        this.port = port;
    }
    public void run(){
        //创建处理i/o事件的线程池
        //处理链接的建立
        NioEventLoopGroup boss = new NioEventLoopGroup();
        //处理存活链接的事件
        NioEventLoopGroup work = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();
        //创建启动辅助类
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss,work)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，
                    // 减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,
                    // 如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG,128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            socketChannel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口，等待绑定成功
            ChannelFuture f = b.bind(port).sync();
            //等到服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务端出错：{}",e.getMessage());
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }



    }




    /*public void run() throws Exception {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        //TODO

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //添加自定义事件消息处理器
                            socketChannel.pipeline().addLast(new DiscardServiceHandler());
                        }
                    })
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG,128)
                    //是否开启TCP底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            //绑定端口，接收进来的连接
            ChannelFuture channelFuture = b.bind(port).sync();
            //等待服务器socket关闭
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }*/
}

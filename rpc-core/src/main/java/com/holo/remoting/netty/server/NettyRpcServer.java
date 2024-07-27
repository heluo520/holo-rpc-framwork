package com.holo.remoting.netty.server;

import com.holo.config.RpcServiceConfig;
import com.holo.factory.SingletonFactory;
import com.holo.provider.ServerProvider;
import com.holo.provider.impl.NaCosServerProviderImpl;
import com.holo.remoting.netty.coder.RpcMessageDecoder;
import com.holo.remoting.netty.coder.RpcMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-25
 * @Description:
 */
@Slf4j
@Component("nettyRpcServer")
public class NettyRpcServer {
    public static final int PORT = 8081;//9998

    private final ServerProvider serviceProvider = SingletonFactory.getInstance(NaCosServerProviderImpl.class);

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    public void run(){

        String host = null;
        try {
            //服务端ip获取本地ip
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        EventLoopGroup boss = new NioEventLoopGroup(1);
            EventLoopGroup work = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(boss,work)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new RpcMessageEncoder())
                                    .addLast(new RpcMessageDecoder())
                                    .addLast(new RpcServerHandler());
                        }
                    });
            log.info("Netty服务器启动-host={},port={}",host,PORT);
            ChannelFuture channelFuture = bootstrap.bind(host, PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务端启动出现异常");
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }
}

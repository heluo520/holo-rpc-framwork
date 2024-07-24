package com.holo.remoting.netty.client;

import com.holo.extend.ExtensionLoader;
import com.holo.factory.SingletonFactory;
import com.holo.provider.ServerProvider;
import com.holo.remoting.api.RpcRequestApi;
import com.holo.remoting.dto.RpcRequest;
import com.holo.remoting.dto.RpcResponse;
import com.holo.remoting.netty.coder.RpcMessageDecoder;
import com.holo.remoting.netty.coder.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description:
 */
public class NettyRpcClient implements RpcRequestApi {
    private final ServerProvider serverProvider;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS))
                                .addLast(new RpcMessageEncoder())
                                .addLast(new RpcMessageDecoder())
                                .addLast(new RpcClientHandler());
                    }
                });
        this.serverProvider = SingletonFactory.getInstance(ServerProvider.class);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @Override
    public RpcResponse sendMessage(RpcRequest request) {
        return null;
    }

    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        return null;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress){
        return null;
    }
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}

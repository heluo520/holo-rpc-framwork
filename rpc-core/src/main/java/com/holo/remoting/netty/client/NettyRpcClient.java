package com.holo.remoting.netty.client;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.holo.config.RpcServiceConfig;
import com.holo.enums.CompressTypeEnum;
import com.holo.enums.SerializationTypeEnum;
import com.holo.extend.ExtensionLoader;
import com.holo.factory.SingletonFactory;
import com.holo.provider.ServerProvider;
import com.holo.register.nacos.Discover;
import com.holo.register.nacos.impl.ServerDiscover;
import com.holo.remoting.RpcRequestTransport;
import com.holo.remoting.constants.RpcConstants;
import com.holo.remoting.dto.RpcMessage;
import com.holo.remoting.dto.RpcRequest;
import com.holo.remoting.dto.RpcResponse;
import com.holo.remoting.netty.coder.RpcMessageDecoder;
import com.holo.remoting.netty.coder.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description:
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final String namingServerAddress = "127.0.0.1:8848";
    private final Discover serverDiscover;
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
        this.serverDiscover = SingletonFactory.getInstance(ServerDiscover.class);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @Override
    public CompletableFuture<RpcResponse<Object>> sendMessage(RpcRequest request) {
        CompletableFuture<RpcResponse<Object>> futureResult = new CompletableFuture<>();
        String rpcServiceName = request.getRpcServiceName();
        Instance instance = null;
        try {
            instance = serverDiscover.discoverService(namingServerAddress,rpcServiceName);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(),e);
        }
        InetSocketAddress inetSocketAddress = new InetSocketAddress(instance.getIp(), instance.getPort());
        Channel channel = getChannel(inetSocketAddress);
        if(channel.isActive()){
            unprocessedRequests.put(request.getRequestId(),futureResult);
            RpcMessage rpcMessage = RpcMessage.builder().data(request)
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .codec(SerializationTypeEnum.KYRO.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener)future -> {
                if(future.isSuccess()){
                    log.info("客户端消息发送成功：{}",rpcMessage);
                }else {
                    log.error("客户端发送消息失败：{}",future.cause().getMessage());
                    future.channel().close();
                    futureResult.completeExceptionally(future.cause());
                }
            });
        }else {
            throw new IllegalStateException();
        }
        return futureResult;
    }
    //请求服务端建立连接
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> future = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) f->{
            if(f.isSuccess()){
                log.info("客户端连接成功:{}",inetSocketAddress.toString());
                future.complete(f.channel());
            }else {
                log.error("客户端连接失败:{}",inetSocketAddress.toString());
                throw new IllegalStateException();
            }
        });
        Channel channel = null;
        try {
            channel = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        return channel;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress){
        Channel channel = channelProvider.getChannel(inetSocketAddress);
        if(channel == null){
            channel = doConnect(inetSocketAddress);
            channelProvider.setChannel(inetSocketAddress,channel);
        }
        return channel;
    }
    //优雅关闭
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}

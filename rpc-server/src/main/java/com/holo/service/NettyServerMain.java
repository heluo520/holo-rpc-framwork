package com.holo.service;

import com.holo.ann.RpcScan;
import com.holo.api.HelloService;
import com.holo.config.RpcServiceConfig;
import com.holo.remoting.netty.server.NettyRpcServer;
import com.holo.service.api.impl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RpcScan(basePackage = {"com.holo"})
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // Register service manually
        HelloService helloService = new HelloServiceImpl();
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //服务在8081端口，ip获取的本地ip
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .ip(host).port(8081)
                .group("test2").version("version2").service(helloService).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.run();
    }
}

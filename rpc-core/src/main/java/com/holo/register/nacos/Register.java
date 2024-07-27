package com.holo.register.nacos;


/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description: 服务注册
 */
public interface Register {
    /**
     * 注册实例
     *
     * @param serverName 要注册的服务名
     * @param ip ip
     * @param port 端口
     * @throws Throwable 异常
     */
    void registerService(String serverName,String ip,int port) throws Throwable;

    /**
     * 注销实例
     * @param namingServerAddress 注册中心地址 ip:port
     * @param serviceName 要注册的服务名
     * @param ip ip
     * @param port 端口
     * @param clusterName 集群名 默认为DEFAULT集群
     */
    void deregisterService(String namingServerAddress,String serviceName,String ip,int port,String clusterName) throws Throwable;
}

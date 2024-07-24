package com.holo.config;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description:
 */
public class RpcServiceConfig {
    private String namingServerAddress;
    private String serviceName;
    private String ip;
    private int port;

    public RpcServiceConfig() {
    }

    public RpcServiceConfig(String namingServerAddress, String serviceName, String ip, int port) {
        this.namingServerAddress = namingServerAddress;
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public RpcServiceConfig ip(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public RpcServiceConfig port(int port) {
        this.port = port;
        return this;
    }

    public String getNamingServerAddress() {
        return namingServerAddress;
    }
    public RpcServiceConfig namingServerAddress(String namingServerAddress) {
        this.namingServerAddress = namingServerAddress;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public RpcServiceConfig serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
}

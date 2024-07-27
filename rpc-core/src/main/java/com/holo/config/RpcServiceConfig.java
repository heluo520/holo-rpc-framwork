package com.holo.config;

import lombok.*;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description:
 */
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
public class RpcServiceConfig {
    private String namingServerAddress = "127.0.0.1:8848";
    private String serviceName;
    private String ip;
    private int port;
    private String version = "";
    private String group = "";
    private Object service;

    public RpcServiceConfig() {
    }

    public RpcServiceConfig(String namingServerAddress, String serviceName, String ip, int port) {
        this.namingServerAddress = namingServerAddress;
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
    }
    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
//    public String getServiceName() {
//        return serviceName;
//    }

}

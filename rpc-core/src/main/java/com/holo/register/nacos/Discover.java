package com.holo.register.nacos;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.holo.config.RpcServiceConfig;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description: 服务发现
 */
public interface Discover {
    /**
     * @param namingServerAddress 注册中心地址
     * @param serviceName ip:port
     * @return Instance 通过负载均衡算法选出的一个健康实例
     * @throws Throwable 异常
     */
    public Instance discoverService(String namingServerAddress,String serviceName) throws Throwable;
}

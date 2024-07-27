package com.holo.provider;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.holo.config.RpcServiceConfig;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description: 用来获取服务与添加服务，是一个抽象接口，底层使用的何种注册中心未知
 */

public interface ServerProvider {
    /**
     * 获取一个服务实例
     * @param rpcServiceName 注册到nacos中的服务名
     * @return 服务实例
     */
    Object getService(String rpcServiceName);

    /**
     * 注册一个服务
     * @param config 服务配置信息
     */
    void addService(RpcServiceConfig config);

    void publishService(RpcServiceConfig rpcServiceConfig);
}

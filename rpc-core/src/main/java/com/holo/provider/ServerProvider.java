package com.holo.provider;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.holo.config.RpcServiceConfig;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description: 用来获取服务与添加服务
 */

public interface ServerProvider {
    /**
     * 获取一个服务实例
     * @param config 服务配置信息
     * @return 服务实例
     */
    Instance getService(RpcServiceConfig config);

    /**
     * 注册一个服务
     * @param config 服务配置信息
     */
    void addService(RpcServiceConfig config);
}

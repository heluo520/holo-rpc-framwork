package com.holo.provider.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.holo.config.RpcServiceConfig;
import com.holo.factory.SingletonFactory;
import com.holo.provider.ServerProvider;
import com.holo.register.nacos.Discover;
import com.holo.register.nacos.Register;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description:
 */
public class NaCosServerProviderImpl implements ServerProvider {
    private static Discover discover;
    private static Register register;
    static {
        discover = SingletonFactory.getInstance(Discover.class);
        register = SingletonFactory.getInstance(Register.class);
    }

    public NaCosServerProviderImpl() {

    }

    @Override
    public Instance getService(RpcServiceConfig config) {
        try {
            return discover.discoverService(config.getNamingServerAddress(),config.getServiceName());
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Override
    public void addService(RpcServiceConfig config) {
        try {
            register.registerService(
                    config.getNamingServerAddress(),
                    config.getServiceName(),
                    config.getIp(),
                    config.getPort()
            );
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}

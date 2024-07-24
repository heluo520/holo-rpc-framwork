package com.holo.register.nacos.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.holo.config.RpcServiceConfig;
import com.holo.register.nacos.Discover;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description: 服务发现
 */
@Slf4j
public class ServerDiscover implements Discover {

    @Override
    public Instance discoverService(String namingServerAddress,String serviceName) throws NacosException {
        NamingService namingService = NamingFactory.createNamingService(namingServerAddress);
        Instance instance = namingService.selectOneHealthyInstance(serviceName);
        log.info("获取的实例：{}",instance);
        return instance;
    }
}

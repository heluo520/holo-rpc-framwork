package com.holo.register.nacos.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.holo.properties.RegisterProperties;
import com.holo.register.nacos.Register;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description: 向nacos注册中心注册服务
 */
public class ServerRegister implements Register {

    @Override
    public void registerService(String namingServerAddress,String serverName,String ip,int port) throws NacosException {
        NamingService namingService = NamingFactory.createNamingService(namingServerAddress);
        namingService.registerInstance(serverName,ip,port);
    }

    @Override
    public void deregisterService(String namingServerAddress, String serviceName, String ip, int port, String clusterName) throws NacosException {
        NamingService namingService = NamingFactory.createNamingService(namingServerAddress);
        if(clusterName==null){
            clusterName = "DEFAULT";
        }
        namingService.deregisterInstance(serviceName,ip,port,clusterName);
    }
}

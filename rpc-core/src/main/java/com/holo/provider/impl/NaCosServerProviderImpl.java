package com.holo.provider.impl;

import com.holo.config.RpcServiceConfig;
import com.holo.enums.RpcErrorMessageEnum;
import com.holo.exception.RpcException;
import com.holo.factory.SingletonFactory;
import com.holo.provider.ServerProvider;
import com.holo.register.nacos.Register;
import com.holo.register.nacos.impl.ServerRegister;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description: nacos实现的服务注册，是一个本地服务注册器，也会同时注册到nacos注册中心
 */
@Slf4j
public class NaCosServerProviderImpl implements ServerProvider {
//    private final Discover discover;
    //服务注册
    private final  Register register;
    //保存服务与实体类的映射
    private final Map<String,Object> serviceMap;
    private final Set<String> registeredService;


    public NaCosServerProviderImpl() {
        register = SingletonFactory.getInstance(ServerRegister.class);
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
    }



    @Override
    public Object getService(String rpcServiceName) {
        Object o = serviceMap.get(rpcServiceName);
        if(o == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND.Message());
        }
        return o;
    }

    @Override
    public void addService(RpcServiceConfig config) {
        String serviceName = config.getRpcServiceName();
        if(!registeredService.contains(serviceName)){
            registeredService.add(serviceName);
            serviceMap.put(serviceName,config.getService());
            log.info("添加服务：{}，对应的接口为：{}",serviceName,config.getService());
        }

    }
    @Override
    public void publishService(RpcServiceConfig config) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            //加入本地注册器
            addService(config);
            //注册到注册中心
            register.registerService(config.getRpcServiceName(),config.getIp(),config.getPort());
            log.info("注册服务成功：serverName={},ip={},port={},hostAddress={}",config.getRpcServiceName(),config.getIp(),config.getPort(),hostAddress);
        } catch (Throwable e) {
            log.info("服务注册失败：{} ,ip: {}",config.getRpcServiceName(),config.getIp());
            e.printStackTrace();
        }


    }
}

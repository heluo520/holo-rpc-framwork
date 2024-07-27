package com.holo.remoting.handler;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.holo.config.RpcServiceConfig;
import com.holo.exception.RpcException;
import com.holo.factory.SingletonFactory;
import com.holo.provider.ServerProvider;
import com.holo.provider.impl.NaCosServerProviderImpl;
import com.holo.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description: 用于服务端根据rpcRequest执行对应的服务方法并返回方法执行结果
 */
@Slf4j
public class TargetMethodHandler {
    private final ServerProvider serverProvider;

    public TargetMethodHandler() {
        this.serverProvider = SingletonFactory.getInstance(NaCosServerProviderImpl.class);
    }

    public Object handle(RpcRequest rpcRequest){
        Object service = serverProvider.getService(rpcRequest.getRpcServiceName());
        log.info("通过服务名获取的本地注册器中的对象：{}",service.getClass().getName());
        return invokeTargetMethod(rpcRequest,service);
    }
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service){

        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            log.info("服务端方法 {} 被执行,返回结果为：{}",method.getName(),result.toString());
            return result;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.info("服务端方法执行失败：{}",e.getMessage());
            e.printStackTrace();
            throw new RpcException(e.getMessage(),e);
        }
    }
}

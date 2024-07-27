package com.holo.remoting.spring;
import com.holo.ann.RpcReference;
import com.holo.ann.RpcService;
import com.holo.config.RpcServiceConfig;
import com.holo.enums.RpcRequestTransportEnum;
import com.holo.extend.ExtensionLoader;
import com.holo.factory.SingletonFactory;
import com.holo.provider.ServerProvider;
import com.holo.provider.impl.NaCosServerProviderImpl;
import com.holo.proxy.RpcClientProxy;
import com.holo.remoting.RpcRequestTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.net.InetAddress;

@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServerProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(NaCosServerProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension(RpcRequestTransportEnum.NETTY.getName());
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // get RpcService annotation
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            String host = InetAddress.getLocalHost().getHostAddress();
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .ip(host)
                    .port(8081)
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            //遍历bean上的字段
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                //被RpcReference标识的为需要消费服务
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                //这个字段即是服务接口类型
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                //获取其代理类
                declaredField.setAccessible(true);
                try {
                    //将代理类注入需要该服务的bean中，这样当这个bean从容器中获取出来的时候这个属性的值就是代理类
                    //执行方法时即是在代理类中进行Netty通信获取服务端方法执行结果
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
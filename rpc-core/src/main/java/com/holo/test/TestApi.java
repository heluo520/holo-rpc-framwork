package com.holo.test;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.holo.config.RpcServiceConfig;
import com.holo.register.nacos.Discover;
import com.holo.register.nacos.Register;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description:
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestApi {
    private Register serverRegister;
    private Discover serverDiscover;
    @GetMapping("/register")
    public String testRegister() throws NacosException {
        /*String url = "http://127.0.0.1:8848/nacos/v1/ns/instance";
        RegisterProperties registerProperties = RegisterProperties.builder()
                .ip("127.0.0.1")
                .port(8082)
                .enable(true)
                .serviceName("rpc-core")
                .build();
        return serverRegister.registerService(url,registerProperties);*/
        log.info("服务注册");
        try {
            serverRegister.registerService("127.0.0.1:8848","rpc-core","127.0.0.1",8080);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "ok";

    }

    @GetMapping("/discover")
    public Instance testDiscover(@RequestParam("serviceName") String serviceName){

        try {
            log.info("服务发现：{}",serviceName);
            RpcServiceConfig config = new RpcServiceConfig().namingServerAddress("127.0.0.1:8848").serviceName(serviceName);
//            Instance instance = serverDiscover.discoverService();
//            return instance;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}

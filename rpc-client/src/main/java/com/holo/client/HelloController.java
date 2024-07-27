package com.holo.client;

import com.holo.ann.RpcReference;
import com.holo.api.Hello;
import com.holo.api.HelloService;
import org.springframework.stereotype.Component;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-25
 * @Description:
 */
@Component
public class HelloController {

    @RpcReference(version = "version1", group = "test1")//消费服务
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = this.helloService.hello(new Hello("111", "222"));
        if("Hello description is 222".equals(hello)){
            System.out.println("客户端成功调用了远程的方法，结果是正确的");
            System.out.println("hello = " + hello);
        }
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.hello(new Hello("111", "222")));
        }
    }
}

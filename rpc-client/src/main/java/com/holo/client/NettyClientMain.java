package com.holo.client;

import com.holo.ann.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-25
 * @Description:
 */
@RpcScan(basePackage = {"com.holo"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        //客户端
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}

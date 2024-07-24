package com.holo.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description: rpc请求数据格式
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable{
    private static final long serialVersionUID = 5820550043664443023L;
    /**
     *
     */
    private String requestId;
    /**
     * 要调用的接口名称
     */
    private String interfaceName;
    /**
     * 要调用的方法名称
     */
    private String methodName;
    /**
     * 方法参数
     */
    private Object[] parameters;
    /**
     * 方法参数类型
     */
    private Class<?>[] paramTypes;
    /**
     * 框架版本
     */
    private String version;
    /**
     *
     */
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}

package com.holo.properties;

import lombok.*;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class RegisterProperties {
    /**
     * 服务实例ip 必选
     */
    private String ip;
    /**
     * 服务实例端口 必选
     */
    private int port;
    /**
     * 命名空间id
     */
    private String namespaceId;
    /**
     * 权重
     */
    private double weight;
    /**
     * 是否上线
     */
    private boolean enable;
    /**
     * 是否健康
     */
    private boolean healthy;
    /**
     * 扩展信息
     */
    private String metadata;
    /**
     * 集群名
     */
    private String clusterName;
    /**
     * 服务名 必选
     */
    private String serviceName;
    /**
     * 分组名
     */
    private String groupName;
    /**
     * 是否是临时实例
     */
    private boolean ephemeral;
}

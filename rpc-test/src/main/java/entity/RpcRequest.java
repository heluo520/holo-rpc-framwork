package entity;

import lombok.*;

import java.io.Serializable;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-18
 * @Description: 客户端请求实体
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class RpcRequest  {
    private String interfaceName;
    private String methodName;
}


package entity;

import lombok.*;

import java.io.Serializable;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-18
 * @Description: 服务端响应实体
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class RpcResponse  {
    private String message;
}

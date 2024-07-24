package com.holo.remoting.api;

import com.holo.remoting.dto.RpcRequest;
import com.holo.remoting.dto.RpcResponse;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description: rpc调用接口
 */
public interface RpcRequestApi {
    public RpcResponse sendMessage(RpcRequest request);
}

package com.holo.remoting.netty.client;

import com.holo.remoting.dto.RpcRequest;
import com.holo.remoting.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description: 存放未处理的请求，与请求被处理时设置结果并移除
 */
@Slf4j
public class UnprocessedRequests {
    /**
     * key为请求id，值为存储响应结果的一个异步对象
     */
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_REQUEST_FUTURES = new ConcurrentHashMap<>();

    /**
     * 添加一个未被handler处理的请求，即还没有响应结果（对于服务端与客户端来说都是一样的）
     * @param requestId
     * @param future
     */
    public  void put(String requestId,CompletableFuture<RpcResponse<Object>> future){
        UNPROCESSED_REQUEST_FUTURES.put(requestId,future);
    }

    /**
     * 处理请求，给请求设置响应结果
     * @param rpcResponse
     */
    public  void complete(RpcResponse<Object> rpcResponse){
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_REQUEST_FUTURES.remove(rpcResponse.getRequestId());
        if(future != null){
            future.complete(rpcResponse);
        }else {
            log.error("找不到对应的未被处理的请求");
            throw new IllegalStateException("找不到对应的未被处理的请求");
        }
    }
}

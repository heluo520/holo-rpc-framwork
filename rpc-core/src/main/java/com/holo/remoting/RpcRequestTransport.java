package com.holo.remoting;

import com.holo.extend.SPI;
import com.holo.remoting.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendMessage(RpcRequest rpcRequest);
}
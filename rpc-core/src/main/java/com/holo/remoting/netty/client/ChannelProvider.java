package com.holo.remoting.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-24
 * @Description: 存储建立的连接通道
 */
@Slf4j
public class ChannelProvider {

    private final Map<String, Channel> channelMap;

    public ChannelProvider() {
        this.channelMap = new ConcurrentHashMap<>();
    }
    public Channel getChannel(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString();
        if(channelMap.containsKey(key)){
            Channel channel = channelMap.get(key);
            if(channel != null && channel.isActive()){
                return channel;
            }else {
                //移除失效的管道
                removeChannel(inetSocketAddress);
            }
        }
        return null;
    }
    public void setChannel(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    public void removeChannel(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("Channel map size :[{}]", channelMap.size());
    }

}

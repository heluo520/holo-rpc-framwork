package com.holo.remoting.serialize;

import com.holo.extend.SPI;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-23
 * @Description: 自动义序列化接口
 */
@SPI
public interface Serializer {
    /**
     * 序列化
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 字节数组
     * @param type 反序列化为对象的类型
     * @return 反序列化后的对象
     * @param <T> 对象类型
     */
    <T> T deserialize(byte[] bytes,Class<T> type);

}

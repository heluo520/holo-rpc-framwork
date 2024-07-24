package com.holo.remoting.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.holo.exception.SerializeException;
import com.holo.remoting.dto.RpcRequest;
import com.holo.remoting.dto.RpcResponse;
import com.holo.remoting.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-23
 * @Description: Kryo方式的序列化方式
 */
@Slf4j
public class KryoSerializer implements Serializer {
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        //默认值为true,是否关闭注册行为,关闭之后可能存在序列化问题，一般推荐设置为 true
        kryo.setReferences(true);
        //默认值为false,是否关闭循环引用，可以提高性能，但是一般不推荐设置为 true
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Output output = new Output(outputStream);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            e.printStackTrace();
            throw new SerializeException("序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            Input input = new Input(inputStream);
            Kryo kryo = kryoThreadLocal.get();
            T t = kryo.readObject(input, type);
            kryoThreadLocal.remove();
            return t;
        }catch (Exception e){
            e.printStackTrace();
            throw new SerializeException("反序列化失败");
        }
    }
}

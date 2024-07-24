package api;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import entity.RpcRequest;
import entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-18
 * @Description: 自定义序列化器
 */
public class KryoSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    //kryo不是线程安全的，所有使用本地线程变量存储
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
            logger.warn("序列化对象 {} 失败",obj);
            return null;
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            Input input = new Input(inputStream);
            Kryo kryo = kryoThreadLocal.get();
            T t = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return t;
        }catch (Exception e){
            e.printStackTrace();
            logger.warn("反序列化为 {} 类型失败",clazz.getName());
            return null;
         }
    }
}

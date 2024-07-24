package utils;

import api.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-18
 * @Description: 自定义编码器，处理出站消息
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;
    /**
     * 要编码的对象类型
     */
    private final Class<?> genericClass;
    /**
     * 将对象转换为字节码然后写入到 ByteBuf 对象中
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(genericClass.isInstance(o)){//是对象
            //序列化为字节码
            byte[] bytes = serializer.serialize(o);
            //写入数据长度
            byteBuf.writeInt(bytes.length);
            //写入字节数组
            byteBuf.writeBytes(bytes);
        }

    }
}

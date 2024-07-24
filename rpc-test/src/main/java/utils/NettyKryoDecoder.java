package utils;

import api.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-18
 * @Description: 自定义解码器，处理入站消息
 */
@AllArgsConstructor
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(NettyKryoDecoder.class);

    private final Serializer serializer;
    /**
     * 要解码的对象类型
     */
    private final Class<?> genericClass;
    /**
     * Netty传输的消息长度也就是对象序列化后对应的字节数组的大小，存储在 ByteBuf 头部
     */
    private static final int BODY_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()>=BODY_LENGTH){
            //标记读指针的位置
            byteBuf.markReaderIndex();
            int len = byteBuf.readInt();
            if(len<0 || byteBuf.readableBytes()<0){
                logger.error("数据长度或byteBuf没有可读字节");
                return;
            }
            //可读字节数小于消息长度，表示消息不完整，重置读指针
            if(byteBuf.readableBytes()<len){
                byteBuf.resetReaderIndex();
                return;
            }

            //正常情况
            byte[] bytes = new byte[len];
            byteBuf.readBytes(bytes);
            Object target = serializer.deserialize(bytes, genericClass);
            list.add(target);
            logger.info("成功反序列化对象：{}",genericClass.getName());
        }
    }
}

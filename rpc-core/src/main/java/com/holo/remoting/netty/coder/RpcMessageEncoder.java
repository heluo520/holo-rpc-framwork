package com.holo.remoting.netty.coder;

import com.holo.compress.Compress;
import com.holo.enums.CompressTypeEnum;
import com.holo.enums.SerializationTypeEnum;
import com.holo.extend.ExtensionLoader;
import com.holo.remoting.constants.RpcConstants;
import com.holo.remoting.dto.RpcMessage;
import com.holo.remoting.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-23
 * @Description: 对RpcMessage进行序列化压缩，即按照自定义协议进行编码
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    //用于生成请求id，是线程安全的一个计数器
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        try {
            byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER);
            byteBuf.writeByte(RpcConstants.VERSION);
            // leave a place to write the value of full length
            //消息总长度暂时还不能确定，先空出4B，将写指针后移4位
            byteBuf.writerIndex(byteBuf.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            byteBuf.writeByte(messageType);
            byteBuf.writeByte(rpcMessage.getCodec());
            byteBuf.writeByte(CompressTypeEnum.GZIP.getCode());
            //请求id
            byteBuf.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // build full length
            byte[] bodyBytes = null;
            //如果消息类型为心跳请求或心跳响应类型则总长度就是头长度16B
            int fullLength = RpcConstants.HEAD_LENGTH;
            // if messageType is not heartbeat message,fullLength = head length + body length
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // serialize the object
                //否则总长度为头+体的长度
                //先序列化再压缩
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: {} ", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // compress the bytes
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                log.info("compress name: {} ", compressName);
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                byteBuf.writeBytes(bodyBytes);
            }
            //记录可写索引位置
            int writeIndex = byteBuf.writerIndex();
            //重置索引为长度字段的起始索引
            byteBuf.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            byteBuf.writeInt(fullLength);
            //重置索引为先前保留的可写索引位置
            byteBuf.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }
    }
}

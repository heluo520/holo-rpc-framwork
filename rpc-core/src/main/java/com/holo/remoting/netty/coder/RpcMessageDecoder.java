package com.holo.remoting.netty.coder;

import com.holo.compress.Compress;
import com.holo.enums.CompressTypeEnum;
import com.holo.enums.SerializationTypeEnum;
import com.holo.extend.ExtensionLoader;
import com.holo.remoting.constants.RpcConstants;
import com.holo.remoting.dto.RpcMessage;
import com.holo.remoting.dto.RpcRequest;
import com.holo.remoting.dto.RpcResponse;
import com.holo.remoting.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-23
 * @Description: 自定义长度解码器
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        // lengthFieldOffset: magic code is 4B, and version is 1B, and then full length. so value is 5
        // lengthFieldLength: full length is 4B. so value is 4
        // lengthAdjustment: full length include all data and read 9 bytes before, so the left length is (fullLength-9). so values is -9
        // initialBytesToStrip: we will check magic code and version manually, so do not strip any bytes. so values is 0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength      发送数据帧的最大长度。 Maximum frame length. It decide the maximum length of data that can be received.
     *                            If it exceeds, the data will be discarded.
     * @param lengthFieldOffset   长度字段的起始索引 Length field offset. The length field is the one that skips the specified length of byte.
     * @param lengthFieldLength   长度字段所占长度 The number of bytes in the length field.
     * @param lengthAdjustment    匹配公式 The compensation value to add to the value of the length field
     * @param initialBytesToStrip 解码时需要去除前多少个字符的值 Number of bytes skipped.
     *                            If you need to receive all of the header+body data, this value is 0
     *                            if you only want to receive the body data, then you need to skip the number of bytes consumed by the header.
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf){
            ByteBuf frame = (ByteBuf) decode;
            if(frame.readableBytes() >= RpcConstants.TOTAL_LENGTH){
                try {
                    return decodeFrame(frame);
                }catch (Exception e){
                    log.error("解码帧出错: {}",e.getMessage());
                    e.printStackTrace();
                } finally {
                    frame.release();
                }
            }
        }
        return decode;
    }

    private Object decodeFrame(ByteBuf in) {
        //将二进制帧解码为RpcMessage类型
        // note: must read ByteBuf in order
        checkMagicNumber(in);
        checkVersion(in);
        //读取4B的长度字段的值，此处长度字段的值被定义为帧的总长度
        int fullLength = in.readInt();
        // build RpcMessage object
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        //读取4B(int类型)的代表请求id的数据
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            //如果为心跳请求类型的消息，则消息体为“ping”
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            //如果心跳响应类型的消息，则消息体为“pong”
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        //消息体的长度 = 帧总长度 - 消息头的长度
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // decompress the bytes 根据压缩码获取压缩类型名，即为扩展配置文件中的key
            String compressName = CompressTypeEnum.getName(compressType);
            log.info("compress name : {} ", compressName);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(compressName);
            bs = compress.decompress(bs);
            // deserialize the object
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec name: {} ", codecName);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;

    }
    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            //自定义帧协议版本不正确
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // read the first 4 bit, which is the magic number, and compare
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                //魔法数不正确
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

}

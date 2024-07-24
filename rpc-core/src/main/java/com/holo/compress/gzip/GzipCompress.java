package com.holo.compress.gzip;

import com.holo.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-23
 * @Description:
 */
public class GzipCompress implements Compress {
    private static final int BUFFER_SIZE = 1024 * 4;
    @Override
    public byte[] compress(byte[] bytes) {
        if(bytes == null){
            throw new IllegalArgumentException("压缩的字节数组为null");
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(bytes);
            gzipOutputStream.flush();
            gzipOutputStream.finish();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip 压缩失败",e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if(bytes == null){
            throw new IllegalArgumentException("解压缩的字节数组为null");
        }
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            byte[] buff = new byte[BUFFER_SIZE];
            int n = 0;
            while ((n=gzipInputStream.read(buff)) > -1){
                byteArrayOutputStream.write(buff,0,n);
            }
            return byteArrayOutputStream.toByteArray();
        }catch (IOException e){
            throw new RuntimeException("gzip 解压缩失败",e);
        }
    }
}

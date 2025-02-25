package com.fool.gamearchivemanager.module.file;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileChannelHandler extends SimpleChannelInboundHandler<Object> {

    private RandomAccessFile randomAccessFile;
    private FileChannel fileChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 在连接时创建一个新文件
        System.out.println("Client connected, starting to receive file...");
        File file = new File("111111.txt");
        randomAccessFile = new RandomAccessFile(file, "rw");
        fileChannel = randomAccessFile.getChannel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("AAAAA");
        if (msg instanceof FileRegion fileRegion) {
            fileRegion.transferTo(fileChannel, fileRegion.position());

        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("File transfer complete, closing file...");
        // 文件接收完成，关闭文件通道
        if (fileChannel != null) {
            fileChannel.close();
        }
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

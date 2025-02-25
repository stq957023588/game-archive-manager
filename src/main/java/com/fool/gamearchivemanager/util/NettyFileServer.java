package com.fool.gamearchivemanager.util;

import com.fool.gamearchivemanager.config.netty.NettyProperties;
import com.fool.gamearchivemanager.module.file.FileChannelInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.jsf.el.SpringBeanFacesELResolver;

@Slf4j
@Getter
public class NettyFileServer implements Runnable {

    private final NettyProperties nettyProperties;

    private Channel channel = null;

    public NettyFileServer(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }

    @Override
    public void run() {
        //创建两个线程组 boosGroup、workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            bootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //使用匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new FileChannelInboundHandler());

                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            log.info("Game archive file transfer server started on port:{}.", nettyProperties.getPort());
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind(nettyProperties.getPort()).sync();
            channel = channelFuture.channel();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

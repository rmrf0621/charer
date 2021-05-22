package com.sharer.server.core.server;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.sharer.server.core.cocurrent.FutureTaskScheduler;
import com.sharer.server.core.distributed.ImWorker;
import com.sharer.server.core.distributed.WorkerRouter;
import com.sharer.server.core.encoder.WebSocketProtobufDecoder;
import com.sharer.server.core.encoder.WebSocketProtobufEncoder;
import com.sharer.server.core.handler.*;
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.utils.JsonUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class ChatServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServer.class);
    // socket
    // 通过nio方式来接收连接和处理连接
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    // 启动引导器
    private ServerBootstrap bootstrap;

    // webscoket
    private EventLoopGroup webBoss;
    private EventLoopGroup webWorker;
    private ServerBootstrap webBootstrap;

    private String ip;

    private Integer port;

    private Integer webPort;


    @Autowired
    private LoginRequestHandler loginRequestHandler;

    @Autowired
    private MessageRequestHandler messageRequestHandler;

    @Autowired
    private RemoteNotificationHandler remoteNotificationHandler;

    @Autowired
    private ServerExceptionHandler serverExceptionHandler;

    /**
     * 读空闲时间(秒)
     */
    public static final int READ_IDLE_TIME = 150;

    /**
     * 写接空闲时间(秒)
     */
    public static final int WRITE_IDLE_TIME = 120;

    /**
     * 心跳响应 超时为30秒
     */
    public static final int PONG_TIME_OUT = 10;

    private ChatServer(ChatServer.Builder builder) {
        this.ip = builder.ip;
        this.port = builder.appPort;
        this.webPort = builder.webPort;
    }

    /**
     * 启动服务
     */
    public void bind() {
        if (this.port != null) {
            this.bindPort();
        }
        if (this.webPort != null) {
            this.bindWebPort();
        }

        ImWorker.getInst().setLocalNode(ip, port, webPort);

        FutureTaskScheduler.add(() -> {
            /**
             * 启动节点
             */
            ImWorker.getInst().init();
            /**
             * 启动节点的管理
             */
            WorkerRouter.getInst().init();
        });


    }

    /**
     * 设置具体的处理逻辑
     *
     * @param pipeline
     */
    private void pipelineSet(ChannelPipeline pipeline) {
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(RequestProto.Request.getDefaultInstance()));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        pipeline.addLast(new IdleStateHandler(READ_IDLE_TIME, WRITE_IDLE_TIME, 0));
        pipeline.addLast("heartBeat", new HeartBeatServerHandler());
        pipeline.addLast("loginRequestHandler", loginRequestHandler);
        pipeline.addLast("messageRequestHandler", messageRequestHandler);
        pipeline.addLast("remoteNotificationHandler", remoteNotificationHandler);
        pipeline.addLast("serverException", serverExceptionHandler);
    }

    /**
     * 绑定socket服务端口
     */
    public void bindPort() {
        //连接监听线程组
        boss = new NioEventLoopGroup(1);
        //传输处理线程组
        worker = new NioEventLoopGroup();
        // 包装类
        bootstrap = new ServerBootstrap();
        //1 设置reactor 线程
        bootstrap.group(boss, worker);
        //2 设置nio类型的channel
        bootstrap.channel(NioServerSocketChannel.class);
        //3 设置监听
        bootstrap.localAddress(new InetSocketAddress(ip, port));
        //4 设置通道选项
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        //5 设置 业务处理流程
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipelineSet(pipeline);
            }
        });

        ChannelFuture channelFuture = bootstrap.bind().syncUninterruptibly();
        channelFuture.channel().newSucceededFuture().addListener(future -> {
            String logBanner = "\n\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "*                   App Socket Server started on port {}.                         *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
            LOGGER.info(logBanner, port);
        });
        channelFuture.channel().closeFuture().addListener(future -> this.destroy(boss, worker));
    }

    /**
     * 绑定webscoket服务端口
     */
    public void bindWebPort() {
        //连接监听线程组
        webBoss = new NioEventLoopGroup(1);
        //传输处理线程组
        webWorker = new NioEventLoopGroup();
        webBootstrap = new ServerBootstrap();
        //1 设置reactor 线程
        webBootstrap.group(webBoss, webWorker);
        //2 设置nio类型的channel
        webBootstrap.channel(NioServerSocketChannel.class);
        //3 设置监听
        webBootstrap.localAddress(new InetSocketAddress(ip, webPort));
        //4 设置通道选项
        webBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        //5 设置 业务处理流程
        webBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // webscoket协议支持
                pipeline.addLast("logging-handler", new LoggingHandler(LogLevel.INFO));
                pipeline.addLast("http-codec", new HttpServerCodec());
                pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                pipeline.addLast(new WebSocketServerCompressionHandler());
                // 协议包长度限制
                pipeline.addLast(new WebSocketServerProtocolHandler("/chat", null, true, 1024 * 10));
                // 协议包解码
                pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                        ByteBuf buf = frame.content();
                        objs.add(buf);
                        buf.retain();
                    }
                });
                //出站处理 将protoBuf实例转为WebSocketFrame
                pipeline.addLast(new ProtobufEncoder() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                        RequestProto.Request request = (RequestProto.Request) msg;
                        System.out.println(JsonUtils.toJSONString(request.getLogin()));
                        WebSocketFrame frame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(request.toByteArray()));
                        out.add(frame);
                    }
                });
                pipeline.addLast(new ProtobufDecoder(RequestProto.Request.getDefaultInstance()));
                pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                //pipeline.addLast(new ProtobufEncoder());
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                pipeline.addLast(new IdleStateHandler(READ_IDLE_TIME, WRITE_IDLE_TIME, 0));
                pipeline.addLast("loginRequestHandler", loginRequestHandler);

                //pipeline.addLast("webSocketProtobufDecoder", new WebSocketProtobufDecoder());
                // 协议包编码
                //pipeline.addLast("webSocketProtobufEncoder", new WebSocketProtobufEncoder());
                //pipelineSet(pipeline);
            }
        });

        ChannelFuture channelFuture = webBootstrap.bind().syncUninterruptibly();
        channelFuture.channel().newSucceededFuture().addListener(future -> {
            String logBanner = "\n\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "*                   webScoket Socket Server started on port {}.                   *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
            LOGGER.info(logBanner, webPort);
        });
        channelFuture.channel().closeFuture().addListener(future -> this.destroy(webBoss, webWorker));
    }

    // 关掉netty
    public void destroy() {
        this.destroy(this.boss, this.worker);
        this.destroy(this.webBoss, this.webWorker);
    }

    public void destroy(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        if (bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown()) {
            try {
                bossGroup.shutdownGracefully();
            } catch (Exception var5) {
            }
        }
        if (workerGroup != null && !workerGroup.isShuttingDown() && !workerGroup.isShutdown()) {
            try {
                workerGroup.shutdownGracefully();
            } catch (Exception var4) {
            }
        }

    }


    public static class Builder {
        private String ip;
        private Integer appPort;
        private Integer webPort;
        //private CIMRequestHandler outerRequestHandler;

        public Builder() {
        }

        public ChatServer.Builder setAppPort(Integer appPort) {
            this.appPort = appPort;
            return this;
        }

        public ChatServer.Builder setWebsocketPort(Integer port) {
            this.webPort = port;
            return this;
        }

        public ChatServer.Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

//        public ChatServer.Builder setOuterRequestHandler(CIMRequestHandler outerRequestHandler) {
//            this.outerRequestHandler = outerRequestHandler;
//            return this;
//        }

        public ChatServer build() {
            return new ChatServer(this);
        }
    }

}

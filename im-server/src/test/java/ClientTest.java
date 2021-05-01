
import com.sharer.server.core.proto.RequestProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientTest {


    public static void main(String[] args) {
        try {
            EventLoopGroup worker = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(worker);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ProtobufVarint32FrameDecoder());
                    pipeline.addLast(new ProtobufDecoder(RequestProto.Request.getDefaultInstance()));
                    pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                    pipeline.addLast(new ProtobufEncoder());
                    pipeline.addLast(new HelloClientIntHandler());
                }
            });

            ChannelFuture f = b.connect("127.0.0.1", 7002).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class HelloClientIntHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //LoginPro.LoginRequest.Builder builders = LoginPro.LoginRequest.newBuilder();
        RequestProto.Request.Builder builders = RequestProto.Request.newBuilder();

        RequestProto.Login.Builder login = RequestProto.Login.newBuilder();
        login.setAccount("100000");
        login.setClientVersion("ios");
        login.setToken("11111QAZ32WQESAD53RGTEFVD");
        login.setDeviceModel("1");
        login.setId(System.currentTimeMillis());
        login.setState(1);
        login.setTimestamp(System.currentTimeMillis());
        login.setSystemVersion("aaaaaaa");
        builders.setCategory(RequestProto.Request.Category.Login);
        builders.setLogin(login);
        ctx.writeAndFlush(builders);

//        int i = 0;
//        while (i < 2) {
//            TimeUnit.SECONDS.sleep(1);
//            System.out.println("=====================123123123========================");
//            ctx.writeAndFlush(createReq(1));
//            i++;
//        }

    }


    public RequestProto.Request createReq(int index) {
        RequestProto.Request.Builder builders = RequestProto.Request.newBuilder();


        RequestProto.Message.Builder message = RequestProto.Message.newBuilder();
        message.setId(10000 + index);
        message.setContent("我也不知道该说些什么");
        message.setCategory(2);
        message.setFrom("2");
        message.setTo("3");
        message.setState(1);
        message.setIsread(1);
        message.setTime(System.currentTimeMillis());

        builders.setMessage(message);
        builders.setCategory(RequestProto.Request.Category.Message);
        return builders.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("=====================读取服务端消息========================");
        RequestProto.Request request = (RequestProto.Request) msg;
        System.out.println(request.toString());
        System.out.println("=====================读取服务端消息========================");
    }
}

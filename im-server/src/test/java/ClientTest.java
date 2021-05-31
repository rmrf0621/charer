
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.utils.JsonUtils;
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

    public static RequestProto.Request createReq(int index) {
        RequestProto.Request.Builder builders = RequestProto.Request.newBuilder();

        RequestProto.Message.Builder message = RequestProto.Message.newBuilder();
        message.setId(10000 + index);
        message.setContent("我也不知道该说些什么");
        message.setMsgType(RequestProto.MsgType.TEXT);
        message.setFrom("2");
        message.setTo("3");
        message.setState(1);
        message.setIsread(1);
        message.setTime(System.currentTimeMillis());

        builders.setMessage(message);
        builders.setCategory(RequestProto.Request.Category.Message);
        return builders.build();
    }
}

class HelloClientIntHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //LoginPro.LoginRequest.Builder builders = LoginPro.LoginRequest.newBuilder();
        RequestProto.Request.Builder builders = RequestProto.Request.newBuilder();

        RequestProto.Login.Builder login = RequestProto.Login.newBuilder();
        login.setAccount("root");
        login.setClientVersion("1");
        login.setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MjMxMzg0ODMsInVzZXJpZCI6MTAwMCwiaWF0IjoxNjIyMjc0NDgzLCJ1c2VybmFtZSI6ImNoYXJsaWUifQ.WR_Z-5W5tJwJMZtTzkN6JgZUPFFVsitJTtAILRoxA9Q");
        login.setDeviceModel("android");
        login.setId(System.currentTimeMillis());
        login.setState(1);
        login.setTimestamp(System.currentTimeMillis());
        login.setSystemVersion("aaaaaaa");
        builders.setCategory(RequestProto.Request.Category.Login);
        builders.setLogin(login);
        ctx.writeAndFlush(builders);
    }


    public RequestProto.Request createReq(int index) {
        RequestProto.Request.Builder builders = RequestProto.Request.newBuilder();

        RequestProto.Message.Builder message = RequestProto.Message.newBuilder();
        message.setId(10000 + index);
        message.setContent("我也不知道该说些什么");
        message.setMsgType(RequestProto.MsgType.TEXT);
        message.setFrom("root");
        message.setTo("charlie");
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
        RequestProto.Message message = request.getMessage();
        System.out.println("发送人:" + message.getFrom() + ",接收人:" + message.getTo() + ",消息内容:" + message.getContent());
        ctx.channel().writeAndFlush(createReq(1));
        System.out.println("=====================读取服务端消息========================");
    }
}

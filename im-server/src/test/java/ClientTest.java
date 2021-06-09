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

import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientTest {

    private final static String account = "nicholas";

    private final static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyVG9rZW5WbyI6IntcInVzZXJpZFwiOjEsXCJ1c2VybmFtZVwiOlwibmljaG9sYXNcIixcImRldmljZVR5cGVcIjpcImFuZHJvaWRcIn0iLCJleHAiOjE2MjM5MTMzMDIsImlhdCI6MTYyMzA0OTMwMn0.jG9NTVSkCN0ULOZxCzrVm2zSHDbsHzij74WjyFfBVd0";

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
                    pipeline.addLast(new HelloClientIntHandler(account,token));
                }
            });

            ChannelFuture f = b.connect("127.0.0.1", 7002).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



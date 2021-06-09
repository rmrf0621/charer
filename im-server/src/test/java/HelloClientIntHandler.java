import com.sharer.server.core.proto.RequestProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

class HelloClientIntHandler extends ChannelInboundHandlerAdapter {

    private final String token;

    private final String account;

    public HelloClientIntHandler(String account, String token) {
        this.token = token;
        this.account = account;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //LoginPro.LoginRequest.Builder builders = LoginPro.LoginRequest.newBuilder();
        RequestProto.Request.Builder builders = RequestProto.Request.newBuilder();
        RequestProto.Login.Builder login = RequestProto.Login.newBuilder();
        login.setAccount(account);
        login.setClientVersion("1");
        login.setToken(token);
        login.setDeviceModel("android");
        login.setId(System.currentTimeMillis());
        login.setState(1);
        login.setTimestamp(System.currentTimeMillis());
        login.setSystemVersion("aaaaaaa");
        builders.setCategory(RequestProto.Request.Category.Login);
        builders.setLogin(login);
        ctx.writeAndFlush(builders);

        Integer index = 0;
        while (true) {
            System.out.println("------"+account+'-'+index);
            ctx.writeAndFlush(createReq(index));
            index++;
            TimeUnit.SECONDS.sleep(15);
        }
    }


    public RequestProto.Request createReq(int index) {
        String to = "charlie";
        RequestProto.Request.Builder builders = RequestProto.Request.newBuilder();
        RequestProto.Message.Builder message = RequestProto.Message.newBuilder();
        message.setId(10000 + index);
        message.setContent(account + "发送给" + to + "的第" + index + "条消息");
        message.setMsgType(RequestProto.MsgType.TEXT);
        message.setFrom(account);
        message.setTo(to);
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
        //ctx.channel().writeAndFlush(createReq(1));
        System.out.println("=====================读取服务端消息========================");
    }
}
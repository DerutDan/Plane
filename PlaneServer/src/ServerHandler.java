import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {
  GameSessionHandlerHelper helper;
  int sessionId;
  ServerHandler(GameSessionHandlerHelper helper) {
    super();
    this.helper = helper;
  }
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    sessionId = helper.aquirePlayer(ctx.channel());
    System.out.println("connected");
  }
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("disconnected");
    helper.endSession(sessionId);
  }
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if(msg instanceof PlayerPacket) {
      helper.aquirePacket((PlayerPacket)msg, sessionId);
    }
  }
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}

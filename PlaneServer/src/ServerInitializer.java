import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
  GameSessionHandlerHelper helper;
  ServerInitializer(GameSessionHandlerHelper helper) {
    super();
    this.helper = helper;
  }

  @Override
  protected void initChannel(SocketChannel socketChannel) throws Exception {
    socketChannel.pipeline()
        //.addLast("decoder", new StringDecoder())
        .addLast("strencoder", new StringEncoder())
        .addLast("byteencoder", new ByteArrayEncoder())
        //.addLast("bytedecoder", new ByteArrayDecoder())
        .addLast("bdec", new ByteToPacketdecoder())
        //.addLast("strtopack", new StrToPacketDecoder())
        .addLast("handler", new ServerHandler(helper));


  }
}

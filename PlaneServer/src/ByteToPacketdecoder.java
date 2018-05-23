import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;


public class ByteToPacketdecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
      List<Object> list) throws Exception {
    int size = byteBuf.readByte();
    int type = byteBuf.readByte();
    list.add(new PlayerActionPacket(channelHandlerContext.channel(), byteBuf.readByte()));
  }
}

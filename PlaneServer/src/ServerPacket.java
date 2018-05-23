import io.netty.channel.Channel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ServerPacket {
  public static byte INIT_PACKET = 0x00, GAME_STATE_PACKET = 0x01; // packet types
  Channel ch;

  ServerPacket(Channel ch) {
    this.ch = ch;
  }
  public Channel getChannel() {
    return ch;
  }

  void write(byte[][] b, byte packetType) {
    ByteBuffer header = ByteBuffer.allocate(5);
    header.order(ByteOrder.LITTLE_ENDIAN);
    int size = 1;
    for(byte[] entry : b) {
      size += entry.length;
    }
    header.putInt(size);
    header.put(packetType);
    ch.write(header.array());

    for(byte[] entry : b) {
      ch.write(entry);
    }
    ch.flush();
  }
  void writeInit(byte playerId,byte playerCount) {
    ByteBuffer bb = ByteBuffer.allocate(7);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    byte[] header = bb.putInt(3).put(INIT_PACKET).put(playerId).put(playerCount).array();
    ch.writeAndFlush(header);
  }
}

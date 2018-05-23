import io.netty.channel.Channel;

public class PlayerInitPacket extends PlayerPacket {

  PlayerInitPacket(Channel ch) {
    super(ch);
  }

  @Override
  void setType() {
    type = 0x01;
  }
}

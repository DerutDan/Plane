import io.netty.channel.Channel;

public abstract class PlayerPacket {
  Channel ch;
  byte type;
  PlayerPacket(Channel ch) {
    this.ch = ch;
    setType();
  }

  public Channel getChannel() {
    return ch;
  }

  public byte getType() {
    return type;
  }

  abstract void  setType();

}

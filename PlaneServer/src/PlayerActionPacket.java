import io.netty.channel.Channel;

public class PlayerActionPacket extends PlayerPacket {
  byte buttonState; // 0 - Upwards pressed, 1 - Upwards released, 2 - Downwards pressed, 4 - Downwards released

  PlayerActionPacket(Channel ch, byte buttonState) {
    super(ch);
    this.buttonState = buttonState;
  }

  @Override
  void setType() {
    type = 0x00;
  }

  public byte getButtonState() {
    return buttonState;
  }
}

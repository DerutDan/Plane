import io.netty.channel.Channel;

public interface GameSessionHandlerHelper {
  int aquirePlayer(Channel channel);
  void aquirePacket(PlayerPacket packet, int SessionId);
  void endSession(int SessionId);
}

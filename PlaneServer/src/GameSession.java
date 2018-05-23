import io.netty.channel.Channel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class GameSession {

  final int playersCount = 2;
  int sessionState = 0; // 0 - not started 1 - started
  int debug = 0;
  ArrayList<Plane> planes;
  ArrayList<Channel> channels;
  ArrayList<Integer> buttonStates; // 0 - Upwards held, 1 - Downwards held, -1 - released

  long lastUpdateTime;

  GameSession() {
    channels = new ArrayList<>();
  }

  void startSession() {
    planes = new ArrayList<>();
    buttonStates = new ArrayList<>();
    planes.add(new Plane(0,0));
    planes.add(new Plane(20,0,true));
    buttonStates.add(-1);
    buttonStates.add(-1);
    for(byte i = 0x00; i < channels.size(); ++i) {
      new ServerPacket(channels.get(i)).writeInit((byte)(i + 1),(byte)playersCount);
    }

    lastUpdateTime = System.currentTimeMillis();
    sessionState = 1;
  }

  public void aquirePacket(PlayerPacket packet) { // Распределение данных, отправленных игроком
    switch(packet.getType()) {
      case 0x00 : {
        aquirePAC((PlayerActionPacket) packet);
        break;
      }
      case 0x01: {
        aquirePlayer(packet.getChannel());
        break;
      }
      default: {
        System.out.println("Unknown packet");
      }
    }
  }
  void aquirePAC(PlayerActionPacket pac) { // Обработка действия клиента
    int buttonAction = pac.getButtonState();
    int k = channels.indexOf(pac.getChannel());
    if(buttonAction == 1 || buttonAction == 3) {
      buttonStates.set(k, -1);
    } else if(buttonAction == 0) {
      buttonStates.set(k, 0);
    } else if(buttonAction == 2) {
      buttonStates.set(k, 1);
    } else if(buttonAction == 4) {
      planes.get(k).shoot();
    }
  }

  public int aquirePlayer(Channel ch) { // Привязка игрока к определенной сессии
    if(channels.size() >= playersCount) {
      return 0;
    } else {
      channels.add(ch);
      if(channels.size() == playersCount) {
        startSession();
      }
      return channels.size() - 1;
    }
  }
  void update() { // Метод обновления игрового состояния

    if ((System.currentTimeMillis() - lastUpdateTime > 1000/24) && sessionState == 1) {
      long dTime = System.currentTimeMillis() - lastUpdateTime;

      //rotate
      for (int i = 0; i < planes.size(); i++) {
        if(buttonStates.get(i) != -1) {
          planes.get(i).rotate(dTime,buttonStates.get(i).equals(0));
        }
      }

      //Bullet intersection
      for(Plane entry1: planes) {
        for(Plane entry2: planes) {
          if(!entry1.equals(entry2)) {
            entry1.intersectsBullets(entry2, dTime);
          }
        }
      }

      //move
      for (Plane entry : planes) {
        entry.move(dTime);
      }

      lastUpdateTime = System.currentTimeMillis();

      //send
      for(Channel entry: channels) {
        new ServerPacket(entry).write(getState(), ServerPacket.GAME_STATE_PACKET);
      }
    }
  }

  byte[][] getState() { // Сериализация игрового состояния
    byte[][] b = new byte[planes.size()][];
    ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    for(int i = 0; i < planes.size(); ++i) {
      b[i] = planes.get(i).getState();
    }
    return b;
  }
}

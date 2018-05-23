import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.concurrent.CopyOnWriteArrayList;



public class Server {
  int port = 4444;
  volatile CopyOnWriteArrayList<GameSession> gs = new CopyOnWriteArrayList<>();
  public void run() {
    EventLoopGroup boss = new NioEventLoopGroup(1);
    EventLoopGroup io = new NioEventLoopGroup(  1);
    try {
      ServerBootstrap server = new ServerBootstrap()
          .group(boss, io)
          .channel(NioServerSocketChannel.class)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childHandler(new ServerInitializer(getHelper()));
      getUpdater().start();
      server.bind(port).sync().channel().closeFuture().sync();

    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      io.shutdownGracefully();
      boss.shutdownGracefully();
    }
  }

  GameSessionHandlerHelper getHelper() {
    return new GameSessionHandlerHelper() {
      @Override
      public int aquirePlayer(Channel channel) {
        if(!gs.isEmpty()) {
          GameSession temp = gs.get(gs.size()-1);
          int success = temp.aquirePlayer(channel);
          if(success == 0) {
            temp = new GameSession();
            temp.aquirePlayer(channel);
            gs.add(temp);
          }
        } else {
          GameSession temp = new GameSession();
          temp.aquirePlayer(channel);
          gs.add(temp);
        }
        return gs.size() - 1;
      }

      @Override
      public void aquirePacket(PlayerPacket packet, int SessionId) {
        gs.get(SessionId).aquirePacket(packet);
      }

      @Override
      public void endSession(int SessionId) {
        if(gs.size() > SessionId) {
          gs.remove(SessionId);
        }
      }
    };
  }

  Thread getUpdater() {
    return new Thread(() -> {
      while(true) {
        if(!gs.isEmpty()) {
          for (GameSession entry: gs) {
            entry.update();
          }
        }
      }
    });
  }

  public static void main(String args[]) {
    new Server().run();
  }
}

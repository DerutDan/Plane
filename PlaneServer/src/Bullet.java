import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Bullet {
  float velocity = 12;
  float currentAngle = 0; // current rotation angle
  float x, y;
  long birthTime;
  final long lifeTime = 20000;
  Plane master;
  Bullet(float x, float y,float currentAngle, Plane master, boolean flipped) {
    this.x = x;
    this.y = y;
    this.currentAngle = currentAngle;
    this.master = master;
    this.birthTime = System.currentTimeMillis();
    if(flipped) {
      velocity *= -1;
    }
  }

  byte[] getState() { // Сереализация пули
    ByteBuffer buffer = ByteBuffer.allocate(8);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.putFloat(x);
    buffer.putFloat(y);
    byte[] bytes = buffer.array();
    return bytes;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  boolean isAlive() {
    return lifeTime > System.currentTimeMillis() - birthTime;
  }

  void move(long dTime) {
    x += Math.cos(Math.toRadians(currentAngle))/1000*velocity*dTime;
    y += Math.sin(Math.toRadians(currentAngle))/1000*velocity*dTime;
  }
}

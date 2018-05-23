import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;

public class Plane {
  float w = 120; // angular speed
  float velocity = 6; // velocity
  float currentAngle = 0; // current rotation angle
  boolean flipped = false; // is plane model flipped, for player 2 is true
  float x,y;
  float width, height;
  long shotDelay = 1000,lastShot = -1;
  ArrayList<Bullet> bullets = new ArrayList<>();

  Plane(float x, float y) {
    this.x = x;
    this.y = y;
    width = 1.81f;
    height  = 0.99f;

  }
  Plane(float x, float y, boolean flipped) {
    this(x,y);
    this.flipped = flipped;
    if(flipped) {
      velocity *= -1;
    }
  }
  void move(long dTime) {
    x += Math.cos(Math.toRadians(currentAngle))/1000*velocity*dTime;
    y += Math.sin(Math.toRadians(currentAngle))/1000*velocity*dTime;
    Iterator<Bullet> iter = bullets.iterator();
    while(iter.hasNext()) {
      Bullet bullet = iter.next();
      if(!bullet.isAlive()) {
        iter.remove();
      } else {
        bullet.move(dTime);
      }
    }
  }
  void rotate(long dTime, boolean upwards) {
    currentAngle += ((upwards ? 1 : -1) * dTime * w/1000) % 360;
  }
  byte[] getState() {
    ByteBuffer buffer = ByteBuffer.allocate(16+bullets.size()*8);//24 - Plane + 4 - Size Bullets + 8*bullets
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.putFloat(x);
    buffer.putFloat(y);
    buffer.putFloat(currentAngle);
    buffer.putInt(bullets.size());
    for(Bullet entry: bullets) {
      buffer.putFloat(entry.getX());
      buffer.putFloat(entry.getY());
    }
    byte[] bytes = buffer.array();
    return bytes;
  }

  void getShot() {
    velocity += flipped ? 1 : -1;
    w -= 10;
  }
  public boolean intersectsBullets(Plane plane, long dTime) {
    if(plane.equals(this)) {
      return false;
    } else {
      for(Bullet entry: bullets) {
        if(plane.intersects(entry, dTime)) {
          bullets.remove(entry);
          return true;
        }
      }
    }
    return false;
  }
  private boolean intersects(Bullet bullet, long dTime) { // Работает неправильно для повернутого самолета

    boolean shot = (Math.abs(bullet.getX() - x) <= width && Math.abs(bullet.getY() - y) <= height);
    if(shot) {
      getShot();
    }
    return shot;
  }

  public void shoot() {
    if(lastShot == -1 || System.currentTimeMillis() - lastShot > shotDelay) {
      float xShift = (float)Math.cos(Math.toRadians(currentAngle)) * width/2*(flipped ? -1 : 1);
      float yShift = (float)Math.sin(Math.toRadians(currentAngle)) * width/2*(flipped ? -1 : 1);

      Bullet bullet = new Bullet(x + xShift, y + yShift, currentAngle, this, flipped);
      lastShot = System.currentTimeMillis();
      bullets.add(bullet);
    }
  }
  public void print() {
    System.out.println(x + " " + y);
  }
}

package components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import components.Ground;
import utility.Resource;

public class Dino {
  private static int dinoBaseY, dinoTopY, dinoStartX, dinoEndX;
  private static int dinoTop, dinoBottom, topPoint;

  private static boolean topPointReached;
  private static int jumpFactor = 20;

  public static final int STAND_STILL = 1,
                    RUNNING = 2,
                    JUMPING = 3,
                    DIE = 4;
  private final int LEFT_FOOT = 1,
                    RIGHT_FOOT = 2,
                    NO_FOOT = 3;
  
  private static int state;
  private int foot;
  private ArrayList<Bullet> bullets = new ArrayList<>();
  private long lastShotTime = 0;
  private static final long SHOOT_COOLDOWN = 500; // 500ms cooldown

  static BufferedImage image;
  BufferedImage leftFootDino;
  BufferedImage rightFootDino;
  BufferedImage deadDino;

  public Dino() {
    image = new Resource().getResourceImage("../images/naruto-stand.png");
    leftFootDino = new Resource().getResourceImage("../images/naruto-run-1.png");
    rightFootDino = new Resource().getResourceImage("../images/naruto-run-2.png");
    deadDino = new Resource().getResourceImage("../images/naruto-dead.png");

    // Lowering the Dino by increasing the Y values
    dinoBaseY = Ground.GROUND_Y + 20; // Increased from +5 to +20
    dinoTopY = Ground.GROUND_Y - image.getHeight() + 20; // Increased from +5 to +20
    dinoStartX = 200;
    dinoEndX = dinoStartX + image.getWidth();
    topPoint = dinoTopY - 120;

    state = STAND_STILL;
    foot = NO_FOOT;
}


  public void create(Graphics g) {
    dinoBottom = dinoTop + image.getHeight();

    switch(state) {
      case STAND_STILL:
        g.drawImage(image, dinoStartX, dinoTopY, null);
        break;

      case RUNNING:
        if(foot == NO_FOOT) {
          foot = LEFT_FOOT;
          g.drawImage(leftFootDino, dinoStartX, dinoTopY, null);
        } else if(foot == LEFT_FOOT) {
          foot = RIGHT_FOOT;
          g.drawImage(rightFootDino, dinoStartX, dinoTopY, null);
        } else {
          foot = LEFT_FOOT;
          g.drawImage(leftFootDino, dinoStartX, dinoTopY, null);
        }
        break;

      case JUMPING:
        if(dinoTop > topPoint && !topPointReached) {
          g.drawImage(image, dinoStartX, dinoTop -= jumpFactor, null);
          break;
        } 
        if(dinoTop >= topPoint && !topPointReached) {
          topPointReached = true;
          g.drawImage(image, dinoStartX, dinoTop += jumpFactor, null);
          break;
        }         
        if(dinoTop > topPoint && topPointReached) {      
          if(dinoTopY == dinoTop && topPointReached) {
            state = RUNNING;
            topPointReached = false;
            break;
          }    
          g.drawImage(image, dinoStartX, dinoTop += jumpFactor, null);          
          break;
        }
      case DIE: 
        g.drawImage(deadDino, dinoStartX, dinoTop, null);    
        break;     
    }

    // Draw bullets
    Iterator<Bullet> bulletIterator = bullets.iterator();
    while (bulletIterator.hasNext()) {
      Bullet bullet = bulletIterator.next();
      if (bullet.isActive()) {
        bullet.draw(g);
      }
    }
  }

  public void shoot() {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastShotTime >= SHOOT_COOLDOWN && state != DIE) {
      bullets.add(new Bullet(dinoEndX, dinoTop + image.getHeight()/2));
      lastShotTime = currentTime;
    }
  }

  public void updateBullets() {
    Iterator<Bullet> bulletIterator = bullets.iterator();
    while (bulletIterator.hasNext()) {
      Bullet bullet = bulletIterator.next();
      bullet.update();
      if (!bullet.isActive()) {
        bulletIterator.remove();
      }
    }
  }

  public ArrayList<Bullet> getBullets() {
    return bullets;
  }

  public void die() {
    state = DIE;
  }

  public static Rectangle getDino() {
    Rectangle dino = new Rectangle();
    dino.x = dinoStartX;

    if(state == JUMPING && !topPointReached) dino.y = dinoTop - jumpFactor;
    else if(state == JUMPING && topPointReached) dino.y = dinoTop + jumpFactor;
    else if(state != JUMPING) dino.y = dinoTop ;

    dino.width = (int) (image.getWidth() * 0.6); // 80% of the original width
    dino.height = (int) (image.getHeight() * 0.6); // 80% of the original height

    return dino;
  }

  public void startRunning() {
    dinoTop = dinoTopY;
    state = RUNNING;
  }

  public void jump() {
    dinoTop = dinoTopY;
    topPointReached = false;
    state = JUMPING;
  }
}
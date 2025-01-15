import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import components.Ground;
import components.Dino;
import components.Obstacles;

class GamePanel extends JPanel implements KeyListener, Runnable {
  
  public static int WIDTH;
  public static int HEIGHT;
  private Thread animator;
  
  private boolean running = false;
  private boolean gameOver = false;
  
  Ground ground;
  Dino dino;
  Obstacles obstacles;

  private int score;
  private int lives = 3;
  private GameOverListener gameOverListener;
  
  public GamePanel() {
    WIDTH = UserInterface.WIDTH;
    HEIGHT = UserInterface.HEIGHT;
    
    ground = new Ground(HEIGHT);
    dino = new Dino();
    obstacles = new Obstacles((int)(WIDTH * 1.5));

    score = 0;
    
    setSize(WIDTH, HEIGHT);
    setVisible(true);
  }

  public void setGameOverListener(GameOverListener listener) {
    this.gameOverListener = listener;
  }
  
  public void paint(Graphics g) {
    super.paint(g);
    
    // Draw score
    g.setFont(new Font("Courier New", Font.BOLD, 25));
    g.drawString(Integer.toString(score), getWidth()/2 - 5, 100);
    
    // Draw lives
    g.setFont(new Font("Arial", Font.BOLD, 20));
    g.setColor(Color.RED);
    g.drawString("Lives: " + lives, 20, 30);
    
    ground.create(g);
    dino.create(g);
    obstacles.create(g);

    if (gameOver) {
      g.setColor(new Color(0, 0, 0, 150));
      g.fillRect(0, 0, getWidth(), getHeight());
      
      g.setColor(Color.WHITE);
      g.setFont(new Font("Arial", Font.BOLD, 40));
      String gameOverText = "Game Over";
      FontMetrics fm = g.getFontMetrics();
      g.drawString(gameOverText, getWidth()/2 - fm.stringWidth(gameOverText)/2, getHeight()/2);
      
      g.setFont(new Font("Arial", Font.PLAIN, 20));
      String instructionText = "Press SPACE to return to menu";
      fm = g.getFontMetrics();
      g.drawString(instructionText, getWidth()/2 - fm.stringWidth(instructionText)/2, getHeight()/2 + 40);
    }
  }
  
  public void run() {
    running = true;

    while(running) {
      updateGame();
      repaint();      
      try {
        Thread.sleep(50);
      } catch(InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void updateGame() {
    score += 1;

    ground.update();
    dino.updateBullets();
    obstacles.update();
    obstacles.checkBulletCollisions(dino.getBullets());

    if(obstacles.hasCollided()) {
      lives--;
      if (lives <= 0) {
        dino.die();
        repaint();
        running = false;
        gameOver = true;
        System.out.println("Game Over - No lives remaining");
      } else {
        obstacles.resume();
        System.out.println("Lost a life - " + lives + " remaining");
      }
    }
  }

  public void reset() {
    score = 0;
    lives = 3;
    System.out.println("reset");
    obstacles.resume();
    gameOver = false;
  }
  
  public void keyTyped(KeyEvent e) {
    if(e.getKeyChar() == ' ') {    
      if(gameOver) {
        if (gameOverListener != null) {
          gameOverListener.onGameOver();
        }
        return;
      }
      if (animator == null || !running) {
        System.out.println("Game starts");        
        animator = new Thread(this);
        animator.start();     
        dino.startRunning();   
      } else {
        dino.jump();
      }
    }
  }
  
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_X) {
      dino.shoot();
    }
  }
  
  public void keyReleased(KeyEvent e) {}

  public interface GameOverListener {
    void onGameOver();
  }
}
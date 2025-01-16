import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.sound.sampled.*;

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
  private Clip backgroundMusic;
  
  public GamePanel() {
    WIDTH = UserInterface.WIDTH;
    HEIGHT = UserInterface.HEIGHT;
    
    ground = new Ground(HEIGHT);
    dino = new Dino();
    obstacles = new Obstacles((int)(WIDTH * 1.5));

    score = 0;
    
    setSize(WIDTH, HEIGHT);
    setVisible(true);
    initializeBackgroundMusic();
  }

  private void initializeBackgroundMusic() {
    try {
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("../sounds/background.wav"));
      backgroundMusic = AudioSystem.getClip();
      backgroundMusic.open(audioStream);
      backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
      System.out.println("Error loading background music: " + e.getMessage());
    }
  }

  private void startBackgroundMusic() {
    if (backgroundMusic != null && !backgroundMusic.isRunning()) {
      backgroundMusic.setFramePosition(0);
      backgroundMusic.start();
    }
  }

  private void stopBackgroundMusic() {
    if (backgroundMusic != null && backgroundMusic.isRunning()) {
      backgroundMusic.stop();
      backgroundMusic.close();
    }
  }

  public void setGameOverListener(GameOverListener listener) {
    this.gameOverListener = listener;
  }
  
  public void paint(Graphics g) {
    super.paint(g);
    
    g.setFont(new Font("Courier New", Font.BOLD, 25));
    g.drawString(Integer.toString(score), getWidth()/2 - 5, 100);
    
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
    startBackgroundMusic();

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
        stopBackgroundMusic();
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
        stopBackgroundMusic();
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
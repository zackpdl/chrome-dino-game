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
import components.ExplosionAnimation;

class GamePanel extends JPanel implements KeyListener, Runnable {

    public static int WIDTH;
    public static int HEIGHT;
    private Thread animator;

    private boolean running = false;
    private boolean gameOver = false;
    private long deathTime = 0;
    private static final long GAME_OVER_DELAY = 500; // 0.5 seconds in milliseconds

    Ground ground;
    Dino dino;
    Obstacles obstacles;
    ExplosionAnimation explosion;
    private BufferedImage backgroundImage; // Background image

    private int score;
    private int lives = 3;
    private GameOverListener gameOverListener;
    private Clip backgroundMusic;
    private Timer repaintTimer;

    // Variables for background movement
    private int bgOffsetX = 0; // Offset for horizontal movement
    private int bgMoveSpeedX = 1; // Speed of horizontal movement (very slow)

    public GamePanel() {
        WIDTH = UserInterface.WIDTH;
        HEIGHT = UserInterface.HEIGHT;

        initializeComponents();

        setSize(WIDTH, HEIGHT);
        setVisible(true);

        try {
            // Load the background image
            backgroundImage = ImageIO.read(new File("images/bg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Background image 'bg.jpg' not found.");
        }

        // Create a timer for continuous repainting
        repaintTimer = new Timer(50, e -> {
            if (running || gameOver) {
                updateBackground(); // Update background movement
                repaint();
            }
        });
    }

    private void initializeComponents() {
        ground = new Ground(HEIGHT);
        dino = new Dino();
        obstacles = new Obstacles((int) (WIDTH * 1.5));
        explosion = new ExplosionAnimation();
        score = 0;
        lives = 3;
    }

    public void setBackgroundMusic(Clip music) {
        this.backgroundMusic = music;
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    private void updateBackground() {
        // Update offset for slow left-to-right movement
        bgOffsetX += bgMoveSpeedX;

        // Reset offset to loop the background
        if (bgOffsetX >= backgroundImage.getWidth()) {
            bgOffsetX = 0;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        // Draw background with horizontal movement
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth();
            int imgHeight = backgroundImage.getHeight();

            // Draw the primary background image
            g2d.drawImage(backgroundImage, -bgOffsetX, 0, imgWidth, HEIGHT, null);

            // Draw the second instance for seamless looping
            g2d.drawImage(backgroundImage, imgWidth - bgOffsetX, 0, imgWidth, HEIGHT, null);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Removed the middle screen score display
        
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        g.drawString("Lives: " + lives, 20, 30);

        g.setFont(new Font("Courier New", Font.BOLD, 25));
        g.setColor(Color.BLACK);
        g.drawString(Integer.toString(score), 20, 60);

        ground.create(g);
        dino.create(g);
        obstacles.create(g);
        explosion.draw(g);

        // Only show game over screen after delay
        if (gameOver && System.currentTimeMillis() - deathTime >= GAME_OVER_DELAY) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String gameOverText = "Game Over";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(gameOverText, getWidth() / 2 - fm.stringWidth(gameOverText) / 2, getHeight() / 2);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String instructionText = "Press SPACE to return to menu";
            fm = g.getFontMetrics();
            g.drawString(instructionText, getWidth() / 2 - fm.stringWidth(instructionText) / 2, getHeight() / 2 + 40);
        }
    }

    @Override
    public void run() {
        running = true;
        repaintTimer.start();

        while (running) {
            updateGame();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void updateGame() {
        if (!running) return;

        score += 1;

        ground.update();
        dino.updateBullets();
        obstacles.update();
        obstacles.checkBulletCollisions(dino.getBullets());
        explosion.update();

        if (obstacles.hasCollided()) {
            lives--;
            if (lives <= 0) {
                dino.die();
                Rectangle dinoBounds = Dino.getDino();
                explosion.start(dinoBounds.x + dinoBounds.width / 2, dinoBounds.y + dinoBounds.height / 2);
                running = false;
                deathTime = System.currentTimeMillis(); // Record time of death
                gameOver = true;
                System.out.println("Game Over - No lives remaining");
            } else {
                obstacles.resume();
                System.out.println("Lost a life - " + lives + " remaining");
            }
        }
    }

    public void reset() {
        if (repaintTimer.isRunning()) {
            repaintTimer.stop();
        }

        running = false;
        if (animator != null) {
            animator.interrupt();
            try {
                animator.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            animator = null;
        }

        initializeComponents();
        gameOver = false;
        deathTime = 0;
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            if (gameOver && System.currentTimeMillis() - deathTime >= GAME_OVER_DELAY) {
                if (gameOverListener != null) {
                    repaintTimer.stop();
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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_X) {
            dino.shoot();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public interface GameOverListener {
        void onGameOver();
    }
}

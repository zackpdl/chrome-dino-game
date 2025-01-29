package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class MainMenu extends JPanel {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final Color BUTTON_COLOR = new Color(83, 83, 83);
    private static final Color HOVER_COLOR = new Color(104, 104, 104);
    
    private final int windowWidth;
    private final int windowHeight;
    private JButton startButton;
    private JButton exitButton;
    private Clip backgroundMusic;
    private BufferedImage backgroundImage;
    
    public MainMenu(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        setLayout(null);
        setPreferredSize(new Dimension(width, height));
        setSize(width, height);
        setVisible(true);
        loadBackgroundImage();
        createButtons();
        initializeBackgroundMusic();
    }
    
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("../images/bg.jpg"));
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }
    }

    private void initializeBackgroundMusic() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("../sounds/background.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        } catch (Exception e) {
            System.out.println("Error loading background music: " + e.getMessage());
        }
    }

    public Clip getBackgroundMusic() {
        return backgroundMusic;
    }

    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
    
    private void createButtons() {
        startButton = createStyledButton("Start Game");
        exitButton = createStyledButton("Exit");
        
        int centerX = (windowWidth - BUTTON_WIDTH) / 2;
        startButton.setBounds(centerX, windowHeight/2 - 50, BUTTON_WIDTH, BUTTON_HEIGHT);
        exitButton.setBounds(centerX, windowHeight/2 + 20, BUTTON_WIDTH, BUTTON_HEIGHT);
        
        add(startButton);
        add(exitButton);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String title = "Chrome Dino Game";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 120);
    }
    
    public JButton getStartButton() {
        return startButton;
    }
    
    public JButton getExitButton() {
        return exitButton;
    }
}
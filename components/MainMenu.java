package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JPanel {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final Color BUTTON_COLOR = new Color(83, 83, 83);
    private static final Color HOVER_COLOR = new Color(104, 104, 104);
    
    private final int windowWidth;
    private final int windowHeight;
    private JButton startButton;
    private JButton exitButton;
    
    public MainMenu(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        setLayout(null);
        setPreferredSize(new Dimension(width, height));
        setSize(width, height);
        setVisible(true);
        createButtons();
    }
    
    private void createButtons() {
        startButton = createStyledButton("Start Game");
        exitButton = createStyledButton("Exit");
        
        // Center the buttons
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
        // Draw background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw title
        g.setColor(Color.BLACK);
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
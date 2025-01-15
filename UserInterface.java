import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import components.MainMenu;

public class UserInterface {
  JFrame mainWindow = new JFrame("T-Rex Run");
  private MainMenu mainMenu;
  private GamePanel gamePanel;
  private Container container;
  
  public static final int WIDTH = 800;
  public static final int HEIGHT = 500;
  
  public void createAndShowGUI() {
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    container = mainWindow.getContentPane();
    container.setLayout(new BorderLayout());
    
    createMainMenu();
    showMainMenu();
    
    mainWindow.setSize(WIDTH, HEIGHT);
    mainWindow.setResizable(false);
    mainWindow.setLocationRelativeTo(null);
    mainWindow.setVisible(true);
  }

  private void createMainMenu() {
    mainMenu = new MainMenu(WIDTH, HEIGHT);
    gamePanel = new GamePanel();
    gamePanel.addKeyListener(gamePanel);
    gamePanel.setFocusable(true);
    
    gamePanel.setGameOverListener(() -> {
      showMainMenu();
    });
    
    mainMenu.getStartButton().addActionListener(e -> {
      startGame();
    });
    
    mainMenu.getExitButton().addActionListener(e -> {
      System.exit(0);
    });
  }

  private void showMainMenu() {
    container.removeAll();
    container.add(mainMenu);
    container.revalidate();
    container.repaint();
  }

  private void startGame() {
    container.removeAll();
    gamePanel.reset();
    container.add(gamePanel);
    gamePanel.requestFocus();
    container.revalidate();
    container.repaint();
  }
  
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new UserInterface().createAndShowGUI();
      }
    });
  }
}
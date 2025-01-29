package components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Bullet {
    private int x;
    private int y;
    private static final int SPEED = 15;
    private static BufferedImage kunaiImage;
    private static final int SIZE = 20;  // Adjusted size for the kunai image
    private boolean active = true;

    static {
        try {
            kunaiImage = ImageIO.read(new File("images/kunai.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load kunai.png");
        }
    }

    public Bullet(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update() {
        x += SPEED;
    }

    public void draw(Graphics g) {
        if (active && kunaiImage != null) {
            g.drawImage(kunaiImage, x, y, SIZE, SIZE, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
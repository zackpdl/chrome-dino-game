package components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;

public class Bullet {
    private int x;
    private int y;
    private static final int SPEED = 15;
    private static final int SIZE = 10;
    private boolean active = true;

    public Bullet(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update() {
        x += SPEED;
    }

    public void draw(Graphics g) {
        if (active) {
            g.setColor(Color.RED);
            g.fillOval(x, y, SIZE, SIZE);
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
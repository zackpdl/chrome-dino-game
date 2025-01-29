package components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ExplosionAnimation {
    private int x, y;
    private boolean isPlaying;
    private int currentFrame;
    private ArrayList<BufferedImage> frames;
    private long lastFrameTime;
    private static final int FRAME_DELAY = 50; // milliseconds between frames

    public ExplosionAnimation() {
        isPlaying = false;
        currentFrame = 0;
        frames = new ArrayList<>();
        loadGif();
    }

    private void loadGif() {
        try {
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream stream = ImageIO.createImageInputStream(getClass().getResourceAsStream("../images/boom.gif"));
            reader.setInput(stream);

            int count = reader.getNumImages(true);
            for (int i = 0; i < count; i++) {
                BufferedImage frame = reader.read(i);
                frames.add(frame);
            }
        } catch (IOException e) {
            System.out.println("Error loading explosion gif: " + e.getMessage());
        }
    }

    public void start(int x, int y) {
        this.x = x;
        this.y = y;
        this.currentFrame = 0;
        this.isPlaying = true;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public void update() {
        if (!isPlaying || frames.isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            currentFrame++;
            lastFrameTime = currentTime;
            
            if (currentFrame >= frames.size()) {
                isPlaying = false;
            }
        }
    }

    public void draw(Graphics g) {
        if (!isPlaying || frames.isEmpty() || currentFrame >= frames.size()) return;
        
        BufferedImage currentImage = frames.get(currentFrame);
        int drawX = x - currentImage.getWidth() / 2;
        int drawY = y - currentImage.getHeight() / 2;
        g.drawImage(currentImage, drawX, drawY, null);
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
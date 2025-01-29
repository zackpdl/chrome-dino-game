package components;

import utility.Resource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.imageio.ImageIO;

public class Ground {

    private class GroundImage {
        BufferedImage image;
        int x;
    }

    public static int GROUND_Y;

    private BufferedImage groundImage;
    private BufferedImage dirtImage;
    private ArrayList<GroundImage> groundImageSet;
    private ArrayList<GroundImage> dirtImageSet;

    public Ground(int panelHeight) {
        GROUND_Y = (int) (panelHeight - 0.25 * panelHeight);

        try {
            groundImage = new Resource().getResourceImage("../images/Ground.png");
            dirtImage = new Resource().getResourceImage("../images/dirt.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        groundImageSet = new ArrayList<>();
        dirtImageSet = new ArrayList<>();

        // Initialize ground and dirt image sets
        for (int i = 0; i < 3; i++) {
            GroundImage ground = new GroundImage();
            ground.image = groundImage;
            ground.x = i * groundImage.getWidth();
            groundImageSet.add(ground);

            GroundImage dirt = new GroundImage();
            dirt.image = dirtImage;
            dirt.x = i * dirtImage.getWidth();
            dirtImageSet.add(dirt);
        }
    }

    public void update() {
        updateImageSet(groundImageSet, groundImage.getWidth());
        updateImageSet(dirtImageSet, dirtImage.getWidth());
    }

    private void updateImageSet(ArrayList<GroundImage> imageSet, int imageWidth) {
        Iterator<GroundImage> looper = imageSet.iterator();
        GroundImage first = looper.next();

        first.x -= 10;

        int previousX = first.x;
        while (looper.hasNext()) {
            GroundImage next = looper.next();
            next.x = previousX + imageWidth;
            previousX = next.x;
        }

        if (first.x < -imageWidth) {
            imageSet.remove(first);
            first.x = previousX + imageWidth;
            imageSet.add(first);
        }
    }

    public void create(Graphics g) {
        // Draw dirt images below the ground
        for (GroundImage dirt : dirtImageSet) {
            g.drawImage(dirt.image, dirt.x, GROUND_Y + 2, null); // Draw dirt at the same level as ground        }

        // Draw ground images
        for (GroundImage ground : groundImageSet) {
            g.drawImage(ground.image, ground.x, GROUND_Y, null);
        }
    }
}
}

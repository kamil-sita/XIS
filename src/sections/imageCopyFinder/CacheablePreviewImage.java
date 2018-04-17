package sections.imageCopyFinder;


import universal.tools.BufferedImageTools.BufferedImageIO;
import universal.tools.BufferedImageTools.BufferedImageScale;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class CacheablePreviewImage {

    private static final int PREVIEW_SIZE = 200;

    private BufferedImage preview;
    private File file;
    private int age;

    public CacheablePreviewImage(File file) {
        BufferedImage image = BufferedImageIO.getImage(file);
        preview = BufferedImageScale.generatePreviewImage(image, PREVIEW_SIZE);
        age = 0;
    }

    public BufferedImage getPreview() {
        return preview;
    }

    public File getFile() {
        return file;
    }

    public void incrementAge() {
        age++;
    }

    public int getAge() {
        return age;
    }

    public void resetAge() {
        age = 0;
    }


}

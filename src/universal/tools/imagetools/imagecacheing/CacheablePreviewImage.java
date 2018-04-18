package universal.tools.imagetools.imagecacheing;


import universal.tools.imagetools.bufferedimagetools.BufferedImageIO;
import universal.tools.imagetools.bufferedimagetools.BufferedImageScale;

import java.awt.image.BufferedImage;
import java.io.File;


public class CacheablePreviewImage {

    private BufferedImage preview;
    private File file;
    private int age;

    public CacheablePreviewImage(File file, int previewSize) {
        this.file = file;
        BufferedImage image = BufferedImageIO.getImage(file);
        preview = BufferedImageScale.generatePreviewImage(image, previewSize);
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

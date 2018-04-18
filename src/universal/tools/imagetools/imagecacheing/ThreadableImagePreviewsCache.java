package universal.tools.imagetools.imagecacheing;

import java.io.File;
import java.util.concurrent.Semaphore;

public class ThreadableImagePreviewsCache {

    private int cacheSize;
    private int previewSize;
    private Semaphore semaphore = new Semaphore(1);

    private CacheablePreviewImage[] cacheablePreviewImages = new CacheablePreviewImage[cacheSize];

    public ThreadableImagePreviewsCache(int cacheSize, int previewSize) {
        this.cacheSize = cacheSize;
        this.previewSize = previewSize;
    }

    public CacheablePreviewImage getImage(File file) {

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ageAllImages();

        //searching for image in already cached images
        for (CacheablePreviewImage previewImage : cacheablePreviewImages) {
            if (previewImage != null) {
                if (previewImage.getFile().equals(file)) {
                    previewImage.resetAge();
                    semaphore.release();
                    return previewImage;

                }
            }

        }

        //checking if there are any free spots

        CacheablePreviewImage cacheablePreviewImage = findSpot(file);
        if (cacheablePreviewImage != null) {
            semaphore.release();
            return cacheablePreviewImage;
        }

        throwAwayOldestImage();

        cacheablePreviewImage = findSpot(file);
        if (cacheablePreviewImage != null) {
            semaphore.release();
            return cacheablePreviewImage;
        }

        throw new RuntimeException("This SHOULD NOT happen");
    }

    private CacheablePreviewImage findSpot(File file) {
        for (int i = 0; i < cacheablePreviewImages.length; i++) {
            if (cacheablePreviewImages[i] == null) {
                cacheablePreviewImages[i] = new CacheablePreviewImage(file, previewSize);
                return cacheablePreviewImages[i];
            }
        }
        return null;
    }

    private void ageAllImages() {
        for (CacheablePreviewImage previewImage : cacheablePreviewImages) {
            if (previewImage != null) {
                previewImage.incrementAge();
            }
        }
    }

    private void throwAwayOldestImage() {
        CacheablePreviewImage oldestImage = null;

        for (CacheablePreviewImage previewImage : cacheablePreviewImages ) {
            if (previewImage != null) {
                if (oldestImage == null) {
                    oldestImage = previewImage;
                } else if (oldestImage.getAge() < previewImage.getAge()) {
                    oldestImage = previewImage;
                }
            }
        }

        if (oldestImage != null) {
            for (int i = 0; i < cacheablePreviewImages.length; i++) {
                if (cacheablePreviewImages[i] == oldestImage) {
                    cacheablePreviewImages[i] = null;
                    return;
                }
            }
        }
    }


}

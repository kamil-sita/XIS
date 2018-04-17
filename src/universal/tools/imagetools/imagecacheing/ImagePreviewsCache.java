package universal.tools.imagetools.imagecacheing;

import java.io.File;

public class ImagePreviewsCache {

    private int cacheSize = 32;
    private int maximumAge = 24;
    private int previewSize;

    private CacheablePreviewImage[] cacheablePreviewImages = new CacheablePreviewImage[cacheSize];

    public ImagePreviewsCache (int cacheSize, int maximumAge, int previewSize) {
        this.cacheSize = cacheSize;
        this.maximumAge = maximumAge;
        this.previewSize = previewSize;
    }

    public CacheablePreviewImage getImage(File file) {

        ageAllImages();

        //searching for image in already cached images
        for (CacheablePreviewImage previewImage : cacheablePreviewImages) {
            if (previewImage != null) {
                if (previewImage.getFile().equals(file)) {
                    previewImage.resetAge();
                    return previewImage;
                }
            }

        }

        //checking if there are any free spots

        CacheablePreviewImage cacheablePreviewImage = findSpot(file);
        if (cacheablePreviewImage != null) return cacheablePreviewImage;

        throwAwayOldestImage();

        cacheablePreviewImage = findSpot(file);
        if (cacheablePreviewImage != null) return cacheablePreviewImage;

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

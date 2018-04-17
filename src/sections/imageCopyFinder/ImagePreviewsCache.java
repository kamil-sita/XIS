package sections.imageCopyFinder;

import java.io.File;

public class ImagePreviewsCache {

    private static final int CACHE_SIZE = 32;
    private static final int MAX_AGE = 24;

    private CacheablePreviewImage[] cacheablePreviewImages = new CacheablePreviewImage[CACHE_SIZE];

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

        for (int i = 0; i < cacheablePreviewImages.length; i++) {
            if (cacheablePreviewImages[i] == null) {
                cacheablePreviewImages[i] = new CacheablePreviewImage(file);
                return cacheablePreviewImages[i];
            }
        }

        throwAwayOldestImage();

        for (int i = 0; i < cacheablePreviewImages.length; i++) {
            if (cacheablePreviewImages[i] == null) {
                cacheablePreviewImages[i] = new CacheablePreviewImage(file);
                return cacheablePreviewImages[i];
            }
        }

        throw new RuntimeException("This SHOULD NOT happen");
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

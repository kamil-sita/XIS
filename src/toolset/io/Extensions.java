package toolset.io;

import javax.imageio.ImageIO;

public class Extensions {

    private static String[] formats;

    private static void lazyGenerateFormats() {
        if (formats != null) return;

        formats = ImageIO.getReaderFormatNames();

        for (int i = 0; i < formats.length; i++) {
            formats[i] = "*." + formats[i];
        }

    }

    /**
     * Gets image formats as supported by ImageIO.
     */
    public static String[] getImageFormats() {
        if (formats == null) {
            lazyGenerateFormats();
        }
        return formats;
    }
}

package toolset.io;

import javax.imageio.ImageIO;

public class ImageExtensions {

    private static String[] starExtensions;
    private static String[] extensions;

    private static void lazyGenerateFormats() {
        if (starExtensions != null && extensions != null) return;

        extensions = ImageIO.getReaderFormatNames();
        starExtensions = ImageIO.getReaderFormatNames();

        for (int i = 0; i < starExtensions.length; i++) {
            starExtensions[i] = "*." + starExtensions[i];
        }

        for (var ex : extensions) {
            System.out.println(ex);
        }

    }

    /**
     * Gets image starExtensions as supported by ImageIO. Returns as "*.extension"
     */
    public static String[] getStarExtensions() {
        lazyGenerateFormats();
        return starExtensions;
    }

    /**
     * Gets image starExtensions as supported by ImageIO. Returns"extension"
     */
    public static String[] getExtensions() {
        lazyGenerateFormats();
        return extensions;
    }
}

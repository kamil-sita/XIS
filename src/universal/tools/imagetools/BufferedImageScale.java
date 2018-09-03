package universal.tools.imagetools;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

    public final class BufferedImageScale {

    /**
     * Scales down image
     * @param source input BufferedImage
     * @param SIZE size of scaled down image
     * @return scaled down image
     */

    public static BufferedImage getScaledDownImage(BufferedImage source, final int SIZE) {
        if (source == null) return null;
        BufferedImage newImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

        final double SIZE_AS_DOUBLE = SIZE * 1.0;

        Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(SIZE_AS_DOUBLE/source.getWidth(), SIZE_AS_DOUBLE/source.getHeight());
        graphics2D.drawImage(source, affineTransform, null);
        graphics2D.dispose();

        return newImage;
    }

}

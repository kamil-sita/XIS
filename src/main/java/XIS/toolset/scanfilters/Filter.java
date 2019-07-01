package XIS.toolset.scanfilters;

import XIS.sections.Interruptible;

import java.awt.image.BufferedImage;

public interface Filter {
    BufferedImage filter(BufferedImage input, Interruptible interruptible);
}

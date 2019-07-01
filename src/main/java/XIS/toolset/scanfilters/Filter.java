package XIS.toolset.scanfilters;

import XIS.sections.Interruptible;

import java.awt.image.BufferedImage;

public interface Filter {
    BufferedImage filter(BufferedImage input, FilterArguments args, Interruptible interruptible);
}

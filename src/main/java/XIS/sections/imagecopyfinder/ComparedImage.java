package XIS.sections.imagecopyfinder;

import java.io.File;
import java.io.Serializable;

public final class ComparedImage implements Serializable {
    private File image;


    public ComparedImage(File image) {
        this.image = image;
    }

    public File getFile() {
        return image;
    }
}

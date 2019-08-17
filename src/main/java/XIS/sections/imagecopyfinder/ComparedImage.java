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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComparedImage that = (ComparedImage) o;

        return image.equals(that.image);
    }

    @Override
    public int hashCode() {
        return image.hashCode();
    }
}

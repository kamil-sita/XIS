package sections.imagecopyfinder;

import java.io.File;

public class GroupedFile {
    private int groupId;
    private CompareGroup compareGroup;
    private File file;

    public GroupedFile(int groupId, CompareGroup compareGroup, File file) {
        this.groupId = groupId;
        this.compareGroup = compareGroup;
        this.file = file;
    }

    public int getGroupId() {
        return groupId;
    }

    public CompareGroup getCompareGroup() {
        return compareGroup;
    }

    public File getFile() {
        return file;
    }
}

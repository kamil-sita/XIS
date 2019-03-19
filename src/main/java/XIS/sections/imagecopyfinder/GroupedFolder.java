package XIS.sections.imagecopyfinder;

import java.io.File;

public class GroupedFolder {
    private int groupId;
    private CompareGroup compareGroup;
    private boolean openRecursively;
    private File folder;

    public GroupedFolder(int groupId, CompareGroup compareGroup, boolean openRecursively, File folder) {
        this.groupId = groupId;
        this.compareGroup = compareGroup;
        this.openRecursively = openRecursively;
        this.folder = folder;
    }

    public GroupedFolder(GroupedFolder template, File newFile){
        this.groupId = template.groupId;
        this.compareGroup = template.compareGroup;
        this.openRecursively = template.openRecursively;
        this.folder = newFile;
    }

    public int getGroupId() {
        return groupId;
    }

    public CompareGroup getCompareGroup() {
        return compareGroup;
    }

    public boolean isOpenRecursively() {
        return openRecursively;
    }

    public File getFolder() {
        return folder;
    }
}

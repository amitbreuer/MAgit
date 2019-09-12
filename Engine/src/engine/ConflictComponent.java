package engine;

import org.apache.commons.codec.digest.DigestUtils;

public class ConflictComponent {
    private Folder.ComponentData oursComponent;
    private Folder.ComponentData theirsComponent;
    private Folder.ComponentData ancestorsComponent;
    private String mergedFileContent;
    private Folder containingFolder;
    private String updater;
    private String dateUpdated;
    private String fileName;
    private String path;

    public ConflictComponent(Folder.ComponentData oursComponent, Folder.ComponentData theirsComponent, Folder.ComponentData ancestorsComponent, Folder containingFolder,String updater,String dateUpdated) {
        this.oursComponent = oursComponent;
        this.theirsComponent = theirsComponent;
        this.ancestorsComponent = ancestorsComponent;
        this.containingFolder = containingFolder;
        this.updater = updater;
        this.dateUpdated = dateUpdated;
        this.fileName = oursComponent != null ? oursComponent.getName() : theirsComponent.getName();
    }

    public String getOursFileContent() {
        return oursComponent != null ? oursComponent.getFolderComponent().toString() : null;
    }

    public String getTheirsFileContent() {
        return theirsComponent != null ? theirsComponent.getFolderComponent().toString() : null;
    }

    public String getAncestorsFileContent() {
        return ancestorsComponent != null ? ancestorsComponent.getFolderComponent().toString() : null;
    }

    public void setMergedFileContent(String mergedFileContent) {
        this.mergedFileContent = mergedFileContent;
    }

    public void updateContainingFolder() {
        Blob mergedBlob = new Blob(mergedFileContent);
        containingFolder.getComponents().add(new Folder.ComponentData(fileName,
                DigestUtils.sha1Hex(mergedFileContent),mergedBlob,updater,dateUpdated));
    }
}


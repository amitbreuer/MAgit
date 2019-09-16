package engine;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;

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

    public ConflictComponent(Folder.ComponentData oursComponent, Folder.ComponentData theirsComponent, Folder.ComponentData ancestorsComponent, Folder containingFolder, String path, String updater, String dateUpdated) {
        this.oursComponent = oursComponent;
        this.theirsComponent = theirsComponent;
        this.ancestorsComponent = ancestorsComponent;
        this.containingFolder = containingFolder;
        this.path = path;
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

    public String GetFullPath(){
        return path + File.separator + fileName;
    }

    public void setMergedFileContent(String mergedFileContent) {
        this.mergedFileContent = mergedFileContent;
    }

    public void updateContainingFolder() {
        Folder.ComponentData resolvedComponent;
        if(oursComponent!= null && mergedFileContent.equals(oursComponent.getFolderComponent().toString())){
            resolvedComponent = oursComponent;
        } else if(theirsComponent != null && mergedFileContent.equals(theirsComponent.getFolderComponent().toString())){
            resolvedComponent = theirsComponent;
        } else if(ancestorsComponent!= null && mergedFileContent.equals(ancestorsComponent.getFolderComponent().toString())){
            resolvedComponent = ancestorsComponent;
        } else {
            resolvedComponent = new Folder.ComponentData(fileName, DigestUtils.sha1Hex(mergedFileContent),
                    new Blob(mergedFileContent), updater, dateUpdated);
        }

        containingFolder.getComponents().add(resolvedComponent);
    }
}

